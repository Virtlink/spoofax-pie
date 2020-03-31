import mb.spoofax.compiler.command.ArgProviderRepr
import mb.spoofax.compiler.command.CommandDefRepr
import mb.spoofax.compiler.command.ParamRepr
import mb.spoofax.compiler.gradle.spoofaxcore.AdapterProjectCompilerSettings
import mb.spoofax.compiler.menu.CommandActionRepr
import mb.spoofax.compiler.menu.MenuItemRepr
import mb.spoofax.compiler.spoofaxcore.*
import mb.spoofax.compiler.util.TypeInfo
import mb.spoofax.core.language.command.CommandContextType
import mb.spoofax.core.language.command.CommandExecutionType

plugins {
  id("org.metaborg.spoofax.compiler.gradle.spoofaxcore.adapter")
  id("org.metaborg.gradle.config.junit-testing")
}

adapterProjectCompiler {
  settings.set(AdapterProjectCompilerSettings(
    parser = ParserCompiler.AdapterProjectInput.builder(),
    styler = StylerCompiler.AdapterProjectInput.builder(),
    completer = CompleterCompiler.AdapterProjectInput.builder(),
    strategoRuntime = StrategoRuntimeCompiler.AdapterProjectInput.builder(),
    constraintAnalyzer = ConstraintAnalyzerCompiler.AdapterProjectInput.builder(),
    compiler = run {
      val packageId = "mb.sdf3.spoofax"
      val taskPackageId = "$packageId.task"
      val commandPackageId = "$packageId.command"

      val builder = AdapterProjectCompiler.Input.builder()


      // Utility task definitions
      val desugar = TypeInfo.of(taskPackageId, "Sdf3Desugar")
      builder.addTaskDefs(desugar)


      // Generation task definitions
      val toCompletionColorer = TypeInfo.of(taskPackageId, "Sdf3ToCompletionColorer")
      val toCompletionRuntime = TypeInfo.of(taskPackageId, "Sdf3ToCompletionRuntime")
      val toCompletion = TypeInfo.of(taskPackageId, "Sdf3ToCompletion")
      val toSignature = TypeInfo.of(taskPackageId, "Sdf3ToSignature")
      val toDynsemSignature = TypeInfo.of(taskPackageId, "Sdf3ToDynsemSignature")
      val toPrettyPrinter = TypeInfo.of(taskPackageId, "Sdf3ToPrettyPrinter")
      val toPermissive = TypeInfo.of(taskPackageId, "Sdf3ToPermissive")
      val toNormalForm = TypeInfo.of(taskPackageId, "Sdf3ToNormalForm")
      val specToParseTable = TypeInfo.of(taskPackageId, "Sdf3SpecToParseTable")
      val specToParenthesizer = TypeInfo.of(taskPackageId, "Sdf3SpecToParenthesizer")
      builder.addTaskDefs(toCompletionColorer, toCompletionRuntime, toCompletion, toSignature, toDynsemSignature,
        toPrettyPrinter, toPermissive, toNormalForm, specToParseTable, specToParenthesizer)


      // Show (debugging) task definitions
      val debugTaskPackageId = "$taskPackageId.debug"
      val showAbstractTaskDef = TypeInfo.of(debugTaskPackageId, "ShowTaskDef")
      val showDesugar = TypeInfo.of(debugTaskPackageId, "Sdf3ShowDesugar")
      val showPermissive = TypeInfo.of(debugTaskPackageId, "Sdf3ShowPermissive")
      val showNormalForm = TypeInfo.of(debugTaskPackageId, "Sdf3ShowNormalForm")
      val showSignature = TypeInfo.of(debugTaskPackageId, "Sdf3ShowSignature")
      val showDynsemSignature = TypeInfo.of(debugTaskPackageId, "Sdf3ShowDynsemSignature")
      val showPrettyPrinter = TypeInfo.of(debugTaskPackageId, "Sdf3ShowPrettyPrinter")
      val showCompletion = TypeInfo.of(debugTaskPackageId, "Sdf3ShowCompletion")
      val showCompletionRuntime = TypeInfo.of(debugTaskPackageId, "Sdf3ShowCompletionRuntime")
      val showCompletionColorer = TypeInfo.of(debugTaskPackageId, "Sdf3ShowCompletionColorer")
      builder.addTaskDefs(
        showDesugar,
        showPermissive,
        showNormalForm,
        showSignature,
        showDynsemSignature,
        showPrettyPrinter,
        showCompletion,
        showCompletionRuntime,
        showCompletionColorer
      )


      // Show (debugging) commands
      fun showCommand(taskDefType: TypeInfo, resultName: String) = CommandDefRepr.builder()
        .type(commandPackageId, taskDefType.id() + "Command")
        .taskDefType(taskDefType)
        .argType(showAbstractTaskDef.appendToId(".Args"))
        .displayName("Show $resultName")
        .description("Shows the $resultName of the file")
        .addSupportedExecutionTypes(CommandExecutionType.ManualOnce, CommandExecutionType.ManualContinuous)
        .addAllParams(listOf(
          ParamRepr.of("file", TypeInfo.of("mb.resource", "ResourceKey"), true, ArgProviderRepr.context(CommandContextType.File)),
          ParamRepr.of("concrete", TypeInfo.ofBoolean(), true)
        ))
        .build()

      val showDesugarCommand = showCommand(showDesugar, "desugared")
      val showPermissiveCommand = showCommand(showPermissive, "permissive grammar")
      val showNormalFormCommand = showCommand(showNormalForm, "normal-form")
      val showSignatureCommand = showCommand(showSignature, "Stratego signatures")
      val showDynsemSignatureCommand = showCommand(showDynsemSignature, "DynSem signatures")
      val showPrettyPrinterCommand = showCommand(showPrettyPrinter, "pretty-printer")
      val showCompletionCommand = showCommand(showCompletion, "completion insertions")
      val showCompletionRuntimeCommand = showCommand(showCompletionRuntime, "completion runtime")
      val showCompletionColorerCommand = showCommand(showCompletionColorer, "completion colorer")
      val showCommands = listOf(
        showDesugarCommand,
        showPermissiveCommand,
        showNormalFormCommand,
        showSignatureCommand,
        showDynsemSignatureCommand,
        showPrettyPrinterCommand,
        showCompletionCommand,
        showCompletionRuntimeCommand,
        showCompletionColorerCommand
      )
      builder.addAllCommandDefs(showCommands)


      // Show (debugging) menu command actions
      fun showManualOnce(commandDef: CommandDefRepr, concrete: Boolean) = CommandActionRepr.builder().manualOnce(commandDef, mapOf(Pair("concrete", concrete.toString()))).fileRequired().buildItem()
      fun showManualContinuous(commandDef: CommandDefRepr, concrete: Boolean) = CommandActionRepr.builder().manualContinuous(commandDef, mapOf(Pair("concrete", concrete.toString()))).fileRequired().buildItem()
      val showAbstractEditorMenuItems = showCommands.flatMap { listOf(showManualOnce(it, false), showManualContinuous(it, false)) }
      val showConcreteEditorMenuItems = showCommands.flatMap { listOf(showManualOnce(it, true), showManualContinuous(it, true)) }
      val showAbstractResourceMenuItems = showCommands.map { showManualOnce(it, false) }
      val showConcreteResourceMenuItems = showCommands.map { showManualOnce(it, true) }


      // Menu bindings
      val mainAndEditorMenu = listOf(
        MenuItemRepr.menu("Debug",
          MenuItemRepr.menu("Transform",
            MenuItemRepr.menu("Abstract", showAbstractEditorMenuItems),
            MenuItemRepr.menu("Concrete", showConcreteEditorMenuItems)
          )
        )
      )
      builder.addAllMainMenuItems(mainAndEditorMenu)
      builder.addAllEditorContextMenuItems(mainAndEditorMenu)
      builder.addResourceContextMenuItems(
        MenuItemRepr.menu("Debug",
          MenuItemRepr.menu("Transform",
            MenuItemRepr.menu("Abstract", showAbstractResourceMenuItems),
            MenuItemRepr.menu("Concrete", showConcreteResourceMenuItems)
          )
        )
      )

      builder
    }
  ))
}

dependencies {
  api("org.metaborg:sdf2parenthesize")

  testAnnotationProcessor(platform("$group:spoofax.depconstraints:$version"))
  testImplementation("org.metaborg:log.backend.slf4j")
  testImplementation("org.slf4j:slf4j-simple:1.7.30")
  testImplementation("org.metaborg:pie.runtime")
  testImplementation("org.metaborg:pie.dagger")
  testCompileOnly("org.checkerframework:checker-qual-android")
  testAnnotationProcessor("com.google.dagger:dagger-compiler")
}

tasks.test {
  // HACK: skip if not in devenv composite build, as that is not using the latest version of SDF3.
  if (gradle.parent == null || gradle.parent!!.rootProject.name != "devenv") {
    onlyIf { false }
  }

  // Show standard out and err in tests during development.
  testLogging {
    events(org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT, org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR)
    showStandardStreams = true
  }
}
