package mb.spoofax.compiler.spoofaxcore;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import mb.resource.DefaultResourceService;
import mb.resource.ResourceService;
import mb.resource.fs.FSPath;
import mb.resource.fs.FSResourceRegistry;
import mb.resource.hierarchical.HierarchicalResource;
import mb.spoofax.compiler.spoofaxcore.util.CommonInputs;
import mb.spoofax.compiler.spoofaxcore.util.FileAssertions;
import mb.spoofax.compiler.spoofaxcore.util.JavaParser;
import mb.spoofax.compiler.util.JavaProject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StrategoRuntimeBuilderCompilerTest {
    @Test void testCompilerDefault() throws IOException {
        final JavaParser javaParser = new JavaParser();
        final ResourceService resourceService = new DefaultResourceService(new FSResourceRegistry());
        final FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        final FSPath baseDirectory = new FSPath(fileSystem.getPath("repo"));

        final Shared shared = CommonInputs.tigerShared(baseDirectory);
        final JavaProject languageProject = CommonInputs.tigerLanguageProjectCompilerInput(shared).project();
        final StrategoRuntimeBuilderCompiler.Input input = CommonInputs.strategoRuntimeBuilderCompilerInput(shared, languageProject);

        final StrategoRuntimeBuilderCompiler compiler = StrategoRuntimeBuilderCompiler.fromClassLoaderResources(resourceService);
        final Charset charset = StandardCharsets.UTF_8;
        final StrategoRuntimeBuilderCompiler.Output output = compiler.compile(input, charset);

        final HierarchicalResource packageDirectory = resourceService.getHierarchicalResource(output.genSourcesJavaDirectory());
        assertTrue(packageDirectory.exists());

        final FileAssertions genFactoryFile = new FileAssertions(resourceService.getHierarchicalResource(output.genStrategoRuntimeBuilderFactoryFile()));
        genFactoryFile.assertName("TigerStrategoRuntimeBuilderFactory.java");
        genFactoryFile.assertExists();
        genFactoryFile.assertContains("class TigerStrategoRuntimeBuilderFactory");
        genFactoryFile.assertJavaParses(javaParser);
    }
}
