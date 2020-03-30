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
import javax.inject.Provider;
import java.io.Serializable;

/**
 * De-explicates the injections of an output AST in the Tiger language.
 */
public final class TigerPostAnalyzeTaskDef extends StrategoTaskDefBase<TigerPostAnalyzeTaskDef.Input, @Nullable IStrategoTerm> {

    public static class Input implements Serializable {
        public final Supplier<@Nullable IStrategoTerm> termSupplier;

        public Input(Supplier<@Nullable IStrategoTerm> termSupplier) {
            this.termSupplier = termSupplier;
        }
    }

    @Inject public TigerPostAnalyzeTaskDef(
        Provider<StrategoRuntime> strategoRuntimeProvider,
        LoggerFactory loggerFactory
    ) {
        super(strategoRuntimeProvider, loggerFactory);
    }

    @Override public @Nullable IStrategoTerm exec(ExecContext context, TigerPostAnalyzeTaskDef.Input input) throws Exception {
        return callStrategy("post-analyze", input.termSupplier.get(context));
    }
}
