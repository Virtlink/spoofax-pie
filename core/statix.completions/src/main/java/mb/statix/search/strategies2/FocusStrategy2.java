package mb.statix.search.strategies2;

import mb.log.api.Logger;
import mb.log.api.LoggerFactory;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.common.strategies.Strategy2;
import mb.statix.search.FocusedSolverState;
import mb.statix.solver.IConstraint;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Stream;


/**
 * Focus on a single constraint.
 */
public final class FocusStrategy2<C extends IConstraint> implements Strategy2<SolverState, FocusedSolverState<C>, SolverContext> {

    private final Class<C> constraintClass;
    private final BiPredicate<C, SolverState> predicate;

    /**
     * Initializes a new instance of the {@link FocusStrategy2} class.
     *
     * @param constraintClass the class of constraints that can be focused on
     * @param predicate the predicate that determine which constraints to focus on
     */
    public FocusStrategy2(Class<C> constraintClass, BiPredicate<C, SolverState> predicate) {
        this.constraintClass = constraintClass;
        this.predicate = predicate;
    }

    @Override
    public List<FocusedSolverState<C>> apply(SolverContext ctx, SolverState input) throws InterruptedException {
        //noinspection unchecked
        Optional<C> focus = input.getConstraints().stream()
                .filter(c -> constraintClass.isAssignableFrom(c.getClass()))
                .map(c -> (C)c)
                .filter(c -> predicate.test(c, input))
                .findFirst();
        if (focus.isPresent()) {
            System.out.println("Focus: " + focus.get());
        } else {
            System.out.println("Focus: NONE");
        }
        return focus.map(c -> Collections.singletonList(new FocusedSolverState<>(input, c))).orElseGet(Collections::emptyList);

    }

    @Override
    public String toString() {
        return "focus(" + constraintClass.getSimpleName() + ")";
    }

}
