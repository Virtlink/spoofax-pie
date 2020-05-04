package mb.statix.search.strategies2;

import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.common.strategies.Strategy;
import mb.statix.common.strategies.Strategy2;
import mb.statix.search.FocusedSolverState;
import mb.statix.solver.IConstraint;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;


/**
 * Unfocuses any constraint.
 */
public final class UnfocusStrategy2<C extends IConstraint> implements Strategy2<FocusedSolverState<C>, SolverState, SolverContext> {

    @Override
    public List<SolverState> apply(SolverContext ctx, FocusedSolverState<C> input) throws InterruptedException {
        return Collections.singletonList(input.getInnerState());

    }

    @Override
    public String toString() {
        return "unfocus";
    }

}
