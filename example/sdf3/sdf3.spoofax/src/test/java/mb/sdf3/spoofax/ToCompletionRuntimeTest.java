package mb.sdf3.spoofax;

import mb.pie.api.ExecException;
import mb.pie.api.MixedSession;
import mb.resource.text.TextResource;
import mb.sdf3.spoofax.task.Sdf3ToCompletionRuntime;
import org.junit.jupiter.api.Test;
import org.spoofax.interpreter.terms.IStrategoTerm;

import javax.annotation.Nullable;

import static org.junit.jupiter.api.Assertions.*;
import static org.spoofax.terms.util.TermUtils.isAppl;
import static org.spoofax.terms.util.TermUtils.isStringAt;

class ToCompletionRuntimeTest extends TestBase {
    @Test void testTask() throws ExecException, InterruptedException {
        final TextResource resource = createTextResource("module nested/a context-free syntax A = <A>", "a.sdf3");
        final Sdf3ToCompletionRuntime taskDef = languageComponent.getToCompletionRuntime();
        try(final MixedSession session = newSession()) {
            final @Nullable IStrategoTerm output = session.require(taskDef.createTask(desugarSupplier(resource)));
            log.info("{}", output);
            assertNotNull(output);
            assertTrue(isAppl(output, "Module"));
            assertTrue(isStringAt(output, 0, "completion/nested/a-cp"));
        }
    }
}
