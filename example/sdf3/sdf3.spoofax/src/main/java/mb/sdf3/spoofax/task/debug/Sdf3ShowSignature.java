package mb.sdf3.spoofax.task.debug;

import mb.sdf3.spoofax.task.Sdf3AnalyzeMulti;
import mb.sdf3.spoofax.task.Sdf3Desugar;
import mb.sdf3.spoofax.task.Sdf3Parse;
import mb.sdf3.spoofax.task.Sdf3ToSignature;
import mb.spoofax.core.language.LanguageScope;
import mb.stratego.common.StrategoRuntime;

import javax.inject.Inject;
import javax.inject.Provider;

@LanguageScope
public class Sdf3ShowSignature extends ShowAnalyzedTaskDef {
    @Inject public Sdf3ShowSignature(
        Sdf3Parse parse,
        Sdf3Desugar desugar,
        Sdf3AnalyzeMulti analyze,
        Sdf3ToSignature operation,
        Provider<StrategoRuntime> strategoRuntimeProvider
    ) {
        super(parse, desugar.createFunction(), analyze, operation.createFunction(), strategoRuntimeProvider, "pp-stratego-string", "Stratego signatures");
    }

    @Override public String getId() {
        return getClass().getName();
    }
}
