package mb.tiger.spoofax.task.reusable;

import mb.common.style.StyleName;
import mb.common.util.ListView;
import mb.completions.common.CompletionProposal;
import mb.completions.common.CompletionResult;
import mb.pie.api.ExecContext;
import mb.pie.api.Supplier;
import mb.pie.api.TaskDef;
import mb.spoofax.core.language.LanguageScope;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spoofax.interpreter.terms.IStrategoTerm;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Objects;

@LanguageScope
public class TigerComplete implements TaskDef<TigerComplete.Input, @Nullable CompletionResult> {

    public static class Input implements Serializable {
        public final Supplier<@Nullable IStrategoTerm> astProvider;

        public Input(Supplier<IStrategoTerm> astProvider) {
            this.astProvider = astProvider;
        }
    }

    public static class Output implements Serializable {

    }

    @Inject public TigerComplete() {}

    @Override
    public String getId() {
        return this.getClass().getName();
    }

    @Override
    public @Nullable CompletionResult exec(ExecContext context, Input input) throws Exception {
        // TODO: get the ast in 'completion mode', with placeholders
        @Nullable IStrategoTerm ast = input.astProvider.get(context);

        return new CompletionResult(ListView.of(
            new CompletionProposal("mypackage", "description", "", "", "mypackage", Objects.requireNonNull(StyleName.fromString("meta.package")), ListView.of(), false),
            new CompletionProposal("myclass", "description", "", "T", "mypackage", Objects.requireNonNull(StyleName.fromString("meta.class")), ListView.of(), false)
        ), true);
    }
}
