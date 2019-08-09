package mb.tiger.spoofax.taskdef.command;

import mb.common.region.Region;
import mb.common.util.EnumSetView;
import mb.common.util.ListView;
import mb.jsglr.common.TermTracer;
import mb.jsglr1.common.JSGLR1ParseResult;
import mb.pie.api.ExecContext;
import mb.pie.api.Task;
import mb.pie.api.TaskDef;
import mb.resource.ResourceKey;
import mb.spoofax.core.language.cli.CliCommand;
import mb.spoofax.core.language.cli.CliCommandItem;
import mb.spoofax.core.language.cli.CliCommandList;
import mb.spoofax.core.language.command.*;
import mb.spoofax.core.language.command.arg.ParamDef;
import mb.spoofax.core.language.command.arg.RawArgs;
import mb.spoofax.core.language.command.arg.TextToResourceKeyArgConverter;
import mb.stratego.common.StrategoUtil;
import mb.tiger.spoofax.taskdef.TigerParse;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spoofax.interpreter.terms.IStrategoTerm;

import javax.inject.Inject;

public class TigerShowParsedAst implements TaskDef<CommandInput<TigerShowArgs>, CommandOutput>, CommandDef<TigerShowArgs> {
    private final TigerParse parse;
    private final TextToResourceKeyArgConverter textToResourceKeyArgConverter;


    @Inject public TigerShowParsedAst(TigerParse parse, TextToResourceKeyArgConverter textToResourceKeyArgConverter) {
        this.parse = parse;
        this.textToResourceKeyArgConverter = textToResourceKeyArgConverter;
    }


    @Override public String getId() {
        return getClass().getName();
    }

    @Override public CommandOutput exec(ExecContext context, CommandInput<TigerShowArgs> input) throws Exception {
        final ResourceKey key = input.args.key;
        final @Nullable Region region = input.args.region;

        final JSGLR1ParseResult parseResult = context.require(parse, key);
        final IStrategoTerm ast = parseResult.getAst()
            .orElseThrow(() -> new RuntimeException("Cannot show parsed AST, parsed AST for '" + key + "' is null"));

        final IStrategoTerm term;
        if(region != null) {
            term = TermTracer.getSmallestTermEncompassingRegion(ast, region);
        } else {
            term = ast;
        }

        final String formatted = StrategoUtil.toString(term);
        return new CommandOutput(ListView.of(CommandFeedbacks.showText(formatted, "Parsed AST for '" + key + "'", null)));
    }

    @Override public Task<CommandOutput> createTask(CommandInput<TigerShowArgs> input) {
        return TaskDef.super.createTask(input);
    }


    @Override public String getDisplayName() {
        return "Show parsed AST";
    }

    @Override public EnumSetView<CommandExecutionType> getSupportedExecutionTypes() {
        return EnumSetView.of(CommandExecutionType.ManualOnce, CommandExecutionType.ManualContinuous);
    }

    @Override public EnumSetView<CommandContextType> getRequiredContextTypes() {
        return EnumSetView.of(CommandContextType.Resource);
    }

    @Override public ParamDef getParamDef() {
        return TigerShowArgs.getParamDef();
    }

    @Override public TigerShowArgs fromRawArgs(RawArgs rawArgs) {
        return TigerShowArgs.fromRawArgs(rawArgs);
    }

    public CliCommandItem getCliCommandItem() {
        final String operation = "parse";
        return CliCommandList.of(operation, "Parses Tiger sources and shows the parsed AST",
            CliCommand.of(this, "file", TigerShowArgs.getFileCliParamDef(operation), "Parses given Tiger file and shows the parsed AST"),
            CliCommand.of(this, "text", TigerShowArgs.getTextCliParamDef(operation, textToResourceKeyArgConverter), "Parses given Tiger text and shows the parsed AST")
        );
    }
}
