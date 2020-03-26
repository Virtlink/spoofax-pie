package mb.tiger.spoofax.task.reusable;

import mb.log.api.Logger;
import mb.log.api.LoggerFactory;
import mb.pie.api.ExecContext;
import mb.pie.api.Supplier;
import mb.pie.api.Task;
import mb.pie.api.TaskDef;
import mb.stratego.common.StrategoException;
import mb.stratego.common.StrategoRuntime;
import mb.stratego.common.StrategoRuntimeBuilder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spoofax.interpreter.terms.IStrategoTerm;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * De-explicates the injections of an output AST in the Tiger language.
 */
public final class TigerPostAnalyzeTaskDef implements TaskDef<TigerPostAnalyzeTaskDef.Input, @Nullable IStrategoTerm> {

    public static class Input implements Serializable {
        public final Supplier<@Nullable IStrategoTerm> termSupplier;

        public Input(Supplier<@Nullable IStrategoTerm> termSupplier) {
            this.termSupplier = termSupplier;
        }
    }

    private final Logger log;
    private final StrategoRuntimeBuilder strategoRuntimeBuilder;
    private final StrategoRuntime prototypeStrategoRuntime;

    @Inject public TigerPostAnalyzeTaskDef(
        StrategoRuntimeBuilder strategoRuntimeBuilder,
        StrategoRuntime prototypeStrategoRuntime,
        LoggerFactory loggerFactory
    ) {
        this.strategoRuntimeBuilder = strategoRuntimeBuilder;
        this.prototypeStrategoRuntime = prototypeStrategoRuntime;
        this.log = loggerFactory.create(TigerPostAnalyzeTaskDef.class);
    }

    @Override public String getId() {
        return getClass().getName();
    }

    @Override public @Nullable IStrategoTerm exec(ExecContext context, Input input) throws Exception {

        @Nullable final IStrategoTerm term = input.termSupplier.get(context);
        if(term == null) {
            log.error("Cannot invoke 'post-analyze' text, got no input.");
            return null;
        }

        final StrategoRuntime strategoRuntime = strategoRuntimeBuilder.buildFromPrototype(prototypeStrategoRuntime);
        final String strategyId = "post-analyze";
        try {
            final @Nullable IStrategoTerm result = strategoRuntime.invoke(strategyId, term);
            if(result == null) {
                log.error("Cannot invoke 'post-analyze' text, executing Stratego strategy '{}' failed with input: {}", strategyId, term);
                return null;
            }
            return result;
        } catch (StrategoException e) {
            log.error("Cannot invoke 'post-analyze' text, executing Stratego strategy '{}' failed with input: {}", e, strategyId, term);
            return null;
        }
    }

    @Override public Task<IStrategoTerm> createTask(Input input) {
        return TaskDef.super.createTask(input);
    }
}
