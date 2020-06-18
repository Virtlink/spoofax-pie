package mb.statix.common.strategies;

import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.solver.log.NullDebugContext;
import mb.statix.solver.persistent.Solver;
import mb.statix.solver.persistent.SolverResult;
import org.metaborg.util.task.NullCancel;
import org.metaborg.util.task.NullProgress;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;


/**
 * Performs inference on the search state.
 *
 * NOTE: Call the isSuccessful() strategy on this result to ensure it has no errors.
 */
public final class InferStrategy2 implements Strategy2<SolverState, SolverState, SolverContext> {

    @Override
    public List<SolverState> apply(SolverContext ctx, SolverState state) throws InterruptedException {

        final SolverResult result = Solver.solve(
                ctx.getSpec(),
                state.getState(),
                state.getConstraints(),
                state.getDelays(),
                state.getCompleteness(),
                new NullDebugContext(),
                new NullProgress(),
                new NullCancel()
        );

        // NOTE: Call the isSuccessful() strategy on this result to ensure it has no errors.

        SolverState newState = SolverState.fromSolverResult(result, state.getExistentials());
        return Collections.singletonList(newState);
    }

    @Override
    public String toString() {
        return "infer";
    }

}
