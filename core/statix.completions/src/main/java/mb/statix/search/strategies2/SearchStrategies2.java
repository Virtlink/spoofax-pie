package mb.statix.search.strategies2;

import mb.nabl2.terms.ITermVar;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.common.strategies.InferStrategy2;
import mb.statix.solver.IConstraint;

import java.util.function.BiPredicate;


/**
 * Convenience functions for creating search strategies.
 */
public final class SearchStrategies2 {

    /**
     * Delays stuck queries in the search state.
     *
     * @return the resulting strategy
     */
    public static DelayStuckQueriesStrategy2 delayStuckQueries() {
        return new DelayStuckQueriesStrategy2();
    }

    /**
     * Expands queries in the search state.
     *
     * @return the resulting strategy
     */
    public static ExpandQueryStrategy2 expandQuery() {
        return new ExpandQueryStrategy2();
    }

//    /**
//     * Expands rules in the search state.
//     *
//     * @return the resulting strategy
//     */
//    public static ExpandRuleStrategy2 expandRule() {
//        return new ExpandRuleStrategy2(focusVar);
//    }
//
    /**
     * Expands rules in the search state.
     *
     * @return the resulting strategy
     */
    public static ExpandRuleStrategy2 expandRule(ITermVar focusVar) {
        return new ExpandRuleStrategy2(focusVar);
    }

    /**
     * Focuses the search state on a particular constraint.
     *
     * @param constraintClass the class of constraints to focus on
     * @param predicate the predicate indicating which constraint to focus on
     * @param <C> the type of constraints to focus on
     * @return the resulting strategy
     */
    public static <C extends IConstraint> FocusStrategy2<C> focus(Class<C> constraintClass, BiPredicate<C, SolverState> predicate) {
        return new FocusStrategy2<>(constraintClass, predicate);
    }

    /**
     * Focuses the search state on a particular constraint, unconditionally.
     *
     * @param constraintClass the class of constraints to focus on
     * @param <C> the type of constraints to focus on
     * @return the resulting strategy
     */
    public static <C extends IConstraint> FocusStrategy2<C> focus(Class<C> constraintClass) {
        return focus(constraintClass, (c, s) -> true);
    }

    /**
     * Performs inference on the search strategy.
     *
     * @return the resulting strategy
     */
    public static InferStrategy2 infer() {
        return new InferStrategy2();
    }

    /**
     * Search strategy that only succeeds if the search state has no errors.
     *
     * @return the resulting strategy
     */
    public static IsSuccessfulStrategy2 isSuccessful() { return new IsSuccessfulStrategy2(); }

    /**
     * Removes Ast ID constraints that where not solved.
     *
     * @return the resulting strategy
     */
    public static RemoveAstIdConstraintsStrategy2 removeAstIdConstraints() {
        return new RemoveAstIdConstraintsStrategy2();
    }

    /**
     * Unfocuses.
     *
     * @return the resulting strategy
     */
    public static <C extends IConstraint> UnfocusStrategy2<C> unfocus() {
        return new UnfocusStrategy2<>();
    }
}
