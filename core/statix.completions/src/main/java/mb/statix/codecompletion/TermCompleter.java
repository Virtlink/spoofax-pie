package mb.statix.codecompletion;
import mb.log.api.Logger;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.common.strategies.Strategy2;
import mb.statix.constraints.CResolveQuery;
import mb.statix.constraints.CUser;
import mb.statix.search.FocusedSolverState;
import mb.statix.search.strategies2.LimitStrategy2;
import mb.statix.search.strategies2.Strategies2;
import one.util.streamex.StreamEx;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static mb.nabl2.terms.build.TermBuild.B;
import static mb.statix.search.strategies2.SearchStrategies2.*;
import static mb.statix.search.strategies2.Strategies2.*;


/**
 * The term completer.
 */
public final class TermCompleter {

//    private static Strategy2<FocusedSolverState<CUser>, SolverState, SolverContext> completionStrategyCont =
////        // @formatter:off
////        seq(expandRule())
////            .$(infer())
////            .$(isSuccessful())
////            .$(delayStuckQueries())
////            .$(repeat(seq(limit(1, focus(CResolveQuery.class)))
////                .$(expandQuery())
////                .$(infer())
////                .$(isSuccessful())
////                .$(delayStuckQueries())
////                .$()
////            ))
//////            .$(id())
////            .$();
////        // @formatter:on
////        // @formatter:off
////            seq(print("Expand Rule: ", expandRule()))
////             .$(print("Infer Rule: ", infer()))
////             .$(print("Success Rule: ", isSuccessful()))
////             .$(delayStuckQueries())
////             .$(repeat(print("Repetition: ", seq(limit(1, focus(CResolveQuery.class)))
////                .$(print("Expand Query: ", expandQuery()))
////                .$(print("Infer Query: ", infer()))
////                .$(print("Success Query: ", isSuccessful()))
////                .$(print("Delay Query: ", delayStuckQueries()))
////                .$()
////             )))
////             .$(print("Result: ", id()))
////             .$();
////            // @formatter:on
//        // @formatter:off
////        seq(print("Expanding rule: ", Strategies2.<FocusedSolverState<CUser>, SolverContext>id()))
////            .$(print("Expand Rule: ", expandRule()))
////            .$(print("Infer Rule: ", infer()))
//           seq(expandRule())
//            .$(infer())
//            .$(isSuccessful())
//            .$(delayStuckQueries())
//            .$(repeat(distinct(seq(limit(1, focus(CResolveQuery.class)))
//                .$(expandQuery())
//                .$(infer())
//                .$(isSuccessful())
//                .$(delayStuckQueries())
//                .$()
//            )))
////            .$(print("Result: ", id()))
//            .$();
//    // @formatter:on

    /**
     * Completes the specified constraint.
     *
     * @param ctx the search context
     * @param state the initial search state
     * @param placeholderVar the var of the placeholder to complete
     * @return the resulting completion proposals
     */
    public List<CompletionSolverProposal> complete(SolverContext ctx, SolverState state, ITermVar placeholderVar) throws InterruptedException {
        ITerm termInUnifier = state.getState().unifier().findRecursive(placeholderVar);
        if (!termInUnifier.equals(placeholderVar)) {
            // The variable we're looking for is already in the unifier
            return Collections.singletonList(new CompletionSolverProposal(state, termInUnifier));
        } else {
            // The variable we're looking for is not in the unifier
            return completeNodes(ctx, state, placeholderVar).stream().map(s -> new CompletionSolverProposal(s, s.project(placeholderVar))).collect(Collectors.toList());
        }
    }

    /**
     * Completes the specified constraint.
     *
     * @param ctx the search context
     * @param state the initial search state
     * @param placeholderVar the var of the placeholder to complete
     * @return the resulting states
     */
    public List<SolverState> completeNodes(SolverContext ctx, SolverState state, ITermVar placeholderVar) throws InterruptedException {
//        return buildCompletionStrategy(placeholderVar, completionStrategyCont).apply(ctx, state);
        return buildCompletionStrategy(placeholderVar).apply(ctx, state);
    }

    private Strategy2<SolverState, SolverState, SolverContext> buildCompletionStrategy(ITermVar placeholderVar) {//}, Strategy2<FocusedSolverState<CUser>, SolverState, SolverContext> continuation) {
        Strategy2<FocusedSolverState<CUser>, SolverState, SolverContext> continuation = buildInnerCompletionStrategy(placeholderVar);
        return distinct(seq(
                seq(limit(1, focus(CUser.class, (c, s) -> constraintContainsVar(s, c, placeholderVar))))
                .$(continuation)
                .$())
            .$(fixSet(
                // Once the variable is no longer present, the focus will fail and the repeat will stop
                seq(limit(1, focus(CUser.class, (c, s) -> constraintContainsVar(s, c, placeholderVar))))
                    .$(continuation)
                    .$()))
            .$());
//            .$(repeat(
//                // Next time we first assert that the variable is still unassigned,
//                // otherwise we break out of the loop
//                seq(/* if isVarUnassigned() then id() else fail() */Strategies.<SolverState, SolverContext>id())
//                .$(limit(1, focus(CUser.class, (c, s) -> constraintContainsVar(s, c, placeholderVar))))
//                .$(continuation)
//                .$())
//            )
//            .$();
//        return seq(limit(1, focus(CUser.class, (c, s) -> constraintContainsVar(s, c, placeholderVar))))
//            .$(continuation)
//            .$();
    }

    private Strategy2<FocusedSolverState<CUser>, SolverState, SolverContext> buildInnerCompletionStrategy(ITermVar placeholderVar) {
        return seq(expandRule(placeholderVar))
                .$(infer())
                .$(isSuccessful())
                .$(delayStuckQueries())
                .$(repeat(distinct(seq(limit(1, focus(CResolveQuery.class)))
                    .$(expandQuery())
                    .$(infer())
                    .$(isSuccessful())
                    .$(delayStuckQueries())
                    .$()
                )))
                .$();
    }

    private static boolean constraintContainsVar(SolverState state, CUser constraint, ITermVar var) {
        // We use the unifier to get all the variables in each of the argument to the constraint
        // (or the constraint argument itself when there where no variables and the argument is a term var)
        // and see if any match the var we're looking for.
        return constraint.args().stream().anyMatch(a -> StreamEx.of(state.getState().unifier().getVars(a))
            .ifEmpty(a instanceof ITermVar ? Stream.of((ITermVar)a) : Stream.empty())
            .anyMatch(var::equals));
    }

    private static boolean isVarUnassigned(SolverState state, ITermVar var) {
        return state.getState().unifier().findRecursive(var) instanceof ITermVar;
    }

//    private static ITerm project(ITermVar placeholderVar, SolverState s) {
//        return s.getState().unifier().findRecursive(placeholderVar);
//    }

    /**
     * A completion solver result.
     */
    public final static class CompletionSolverProposal {
        private final SolverState newState;
        private final ITerm term;

        public CompletionSolverProposal(SolverState newState, ITerm term) {
            this.newState = newState;
            this.term = term;
        }

        public SolverState getNewState() {
            return newState;
        }

        public ITerm getTerm() {
            return term;
        }
    }
}
