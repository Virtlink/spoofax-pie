package mb.sdf3.spoofax.task;

import mb.spoofax.core.language.LanguageScope;
import mb.stratego.common.StrategoRuntime;

import javax.inject.Inject;
import javax.inject.Provider;

@LanguageScope
public class Sdf3ToSignature extends StrategoTransformAnalyzedTaskDef {
    @Inject public Sdf3ToSignature(Provider<StrategoRuntime> strategoRuntimeProvider) {
        super(strategoRuntimeProvider, "module-to-sig");
    }

    @Override public String getId() {
        return getClass().getName();
    }
}
