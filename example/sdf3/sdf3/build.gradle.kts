import mb.spoofax.compiler.spoofaxcore.*
import mb.spoofax.compiler.util.GradleDependency

plugins {
  id("org.metaborg.spoofax.compiler.gradle.spoofaxcore.language")
  id("org.metaborg.gradle.config.junit-testing")
  id("de.set.ecj") // Use ECJ to speed up compilation of Stratego's generated Java files.
}

dependencies {
  api(platform("org.metaborg:spoofax.depconstraints:$version"))
  testImplementation("org.metaborg:log.backend.slf4j")
  testImplementation("org.slf4j:slf4j-simple:1.7.30")
  testCompileOnly("org.checkerframework:checker-qual-android")
}

languageProjectCompiler {
  settings.set(mb.spoofax.compiler.gradle.spoofaxcore.LanguageProjectCompilerSettings(
    parser = ParserCompiler.LanguageProjectInput.builder()
      .startSymbol("Module"),
    styler = StylerCompiler.LanguageProjectInput.builder(),
    completer = CompleterCompiler.LanguageProjectInput.builder(),
    strategoRuntime = StrategoRuntimeCompiler.LanguageProjectInput.builder()
      .addInteropRegisterersByReflection("org.metaborg.meta.lang.template.strategies.InteropRegisterer")
      .enableNaBL2(false)
      .enableStatix(true)
      .copyCTree(true)
      .copyClasses(false)
      .copyJavaStrategyClasses(true)
      .classKind(mb.spoofax.compiler.util.ClassKind.Extended)
      .manualFactory("mb.sdf3", "Sdf3ManualStrategoRuntimeBuilderFactory"),
    constraintAnalyzer = ConstraintAnalyzerCompiler.LanguageProjectInput.builder()
      .strategoStrategy("statix-editor-analyze")
      .multiFile(true),
    compiler = run {
      val builder = LanguageProjectCompiler.Input.builder()
      builder.addAdditionalCopyResources("target/metaborg/EditorService-pretty.pp.af")
      if (gradle.parent != null && gradle.parent!!.rootProject.name == "devenv") {
        // HACK: use org.metaborggggg groupId for SDF3, as that is used to prevent bootstrapping issues.
        builder.languageSpecificationDependency(GradleDependency.module("org.metaborggggg:org.metaborg.meta.lang.template:2.6.0-SNAPSHOT"))
      } else {
        // HACK: when building standalone (outside of devenv composite build), use a normal SDF3 dependency.
        builder.languageSpecificationDependency(GradleDependency.module("org.metaborg:org.metaborg.meta.lang.template:2.6.0-SNAPSHOT"))
      }
      builder
    }
  ))
}

ecj {
  toolVersion = "3.20.0"
}
