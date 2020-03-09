package mb.tiger.spoofax.task.reusable;

import mb.common.region.Region;
import mb.common.util.ListView;
import mb.jsglr.common.TermTracer;
import mb.jsglr1.common.JSGLR1ParseResult;
import mb.log.api.Logger;
import mb.log.api.LoggerFactory;
import mb.pie.api.ExecContext;
import mb.pie.api.ResourceStringSupplier;
import mb.pie.api.Supplier;
import mb.pie.api.Task;
import mb.pie.api.TaskDef;
import mb.resource.ResourceKey;
import mb.spoofax.core.language.command.CommandFeedback;
import mb.spoofax.core.language.command.CommandOutput;
import mb.stratego.common.StrategoException;
import mb.stratego.common.StrategoRuntime;
import mb.stratego.common.StrategoRuntimeBuilder;
import mb.stratego.common.StrategoUtil;
import mb.tiger.spoofax.task.TigerShowArgs;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.terms.IStrategoTerm;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * Pretty-prints an input AST to a string in the Tiger language.
 */
public final class TigerPrettyPrintTaskDef implements TaskDef<TigerPrettyPrintTaskDef.Input, @Nullable String> {

    public static class Input implements Serializable {
        public final Supplier<@Nullable IStrategoTerm> termSupplier;

        public Input(Supplier<@Nullable IStrategoTerm> termSupplier) {
            this.termSupplier = termSupplier;
        }
    }

    private final Logger log;
    private final TigerParse parse;
    private final StrategoRuntimeBuilder strategoRuntimeBuilder;
    private final StrategoRuntime prototypeStrategoRuntime;

    @Inject public TigerPrettyPrintTaskDef(
        TigerParse parse,
        StrategoRuntimeBuilder strategoRuntimeBuilder,
        StrategoRuntime prototypeStrategoRuntime,
        LoggerFactory loggerFactory
    ) {
        this.parse = parse;
        this.strategoRuntimeBuilder = strategoRuntimeBuilder;
        this.prototypeStrategoRuntime = prototypeStrategoRuntime;
        this.log = loggerFactory.create(TigerPrettyPrintTaskDef.class);
    }

    @Override public String getId() {
        return getClass().getName();
    }

    @Override public @Nullable String exec(ExecContext context, Input input) throws Exception {

        final IStrategoTerm term = input.termSupplier.get(context);

        final StrategoRuntime strategoRuntime = strategoRuntimeBuilder.buildFromPrototype(prototypeStrategoRuntime);
        final String strategyId = "pp-partial-Tiger-string";
        try {
            final @Nullable IStrategoTerm result = strategoRuntime.invoke(strategyId, term);
            if(result == null) {
                log.error("Cannot show pretty-printed text, executing Stratego strategy '{}' failed with input: {}", strategyId, term);
                return null;
            }
            return StrategoUtil.toString(result);
        } catch (StrategoException e) {
            log.error("Cannot show pretty-printed text, executing Stratego strategy '{}' failed with input: {}", e, strategyId, term);
            return null;
        }
    }

    @Override public Task<String> createTask(Input input) {
        return TaskDef.super.createTask(input);
    }
}
