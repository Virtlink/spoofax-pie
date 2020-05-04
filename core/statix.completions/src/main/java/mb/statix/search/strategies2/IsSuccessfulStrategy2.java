package mb.statix.search.strategies2;

import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.unification.UnifierFormatter;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.common.strategies.Strategy;
import mb.statix.common.strategies.Strategy2;
import mb.statix.constraints.messages.IMessage;
import mb.statix.solver.IConstraint;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;


/**
 * Search strategy that only succeeds if the search state has no errors.
 */
public final class IsSuccessfulStrategy2 implements Strategy2<SolverState, SolverState, SolverContext> {

    @Override
    public List<SolverState> apply(SolverContext ctx, SolverState state) throws InterruptedException {
        if (state.hasErrors()) {
            System.out.println("isSuccessful(): Rejected because:");
            for (java.util.Map.Entry<IConstraint, IMessage> e : state.getMessages().entrySet()) {
                System.out.println("- " + e.getValue().toString(ITerm::toString));
                System.out.println("  " + e.getKey().toString(t -> new UnifierFormatter(state.getState().unifier(), 8).format(t)));
            }
            return Collections.emptyList();
        } else {
            System.out.println("isSuccessful(): Accepted");
            return Collections.singletonList(state);
        }
    }

    @Override
    public String toString() {
        return "isSuccessful";
    }

}
