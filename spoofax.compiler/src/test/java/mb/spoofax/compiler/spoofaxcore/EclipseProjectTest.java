package mb.spoofax.compiler.spoofaxcore;

import mb.resource.fs.FSPath;
import mb.spoofax.compiler.spoofaxcore.tiger.TigerInputs;
import mb.spoofax.compiler.util.GradleDependency;
import mb.spoofax.compiler.util.TemplateCompiler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

class EclipseProjectTest extends TestBase {
    @Test void testCompiler(@TempDir Path temporaryDirectoryPath) throws IOException {
        final FSPath baseDirectory = new FSPath(temporaryDirectoryPath);
        final Shared shared = TigerInputs.shared(baseDirectory);

        // Compile language project, as adapter project depends on it.
        final Parser parserCompiler = Parser.fromClassLoaderResources(resourceService, charset);
        final Styler stylerCompiler = Styler.fromClassLoaderResources(resourceService, charset);
        final StrategoRuntime strategoRuntimeCompiler = StrategoRuntime.fromClassLoaderResources(resourceService, charset);
        final ConstraintAnalyzer constraintAnalyzerCompiler = ConstraintAnalyzer.fromClassLoaderResources(resourceService, charset);
        final LanguageProject languageProjectCompiler = LanguageProject.fromClassLoaderResources(resourceService, charset, parserCompiler, stylerCompiler, strategoRuntimeCompiler, constraintAnalyzerCompiler);
        languageProjectCompiler.compile(TigerInputs.languageProject(shared));

        // Compile adapter project, as Eclipse project depends on it.
        final AdapterProject.Input adapterProjectInput = TigerInputs.adapterProjectBuilder(shared)
            .languageProjectDependency(GradleDependency.project(":" + shared.languageProject().coordinate().artifactId()))
            .build();
        TigerInputs.copyTaskDefsIntoAdapterProject(adapterProjectInput, resourceService);
        final AdapterProject adapterProjectCompiler = AdapterProject.fromClassLoaderResources(resourceService, charset, parserCompiler, stylerCompiler, strategoRuntimeCompiler, constraintAnalyzerCompiler);
        adapterProjectCompiler.compile(adapterProjectInput);

        // Compile Eclipse project and test generated files.
        final EclipseProject.Input input = TigerInputs.eclipseProjectBuilder(shared, adapterProjectInput)
            .adapterProjectDependency(GradleDependency.project(":" + shared.adapterProject().coordinate().artifactId()))
            .build();
        final EclipseProject compiler = new EclipseProject(new TemplateCompiler(EclipseProject.class, resourceService, charset));
        compiler.compile(input);
        fileAssertions.asserts(input.buildGradleKtsFile(), (a) -> a.assertContains("org.metaborg.coronium.bundle"));
        fileAssertions.scopedExists(input.classesGenDirectory(), (s) -> {
            s.assertPublicJavaClass(input.plugin(), "TigerPlugin");
        });

        // Compile root project, which links together language and adapter project, and build it.
        final RootProject.Output rootProjectOutput = rootProjectCompiler.compile(TigerInputs.rootProjectBuilder(shared)
            .addIncludedProjects(
                shared.languageProject().coordinate().artifactId(),
                shared.adapterProject().coordinate().artifactId(),
                shared.eclipseProject().coordinate().artifactId()
            )
            .build()
        );
        fileAssertions.asserts(rootProjectOutput.baseDirectory(), (a) -> a.assertGradleBuild("buildAll"));
    }
}
