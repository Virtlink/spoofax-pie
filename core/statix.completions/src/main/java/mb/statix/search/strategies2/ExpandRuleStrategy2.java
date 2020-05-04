package mb.statix.search.strategies2;

import com.google.common.collect.ImmutableSet;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.common.strategies.Strategy2;
import mb.statix.constraints.CUser;
import mb.statix.search.FocusedSolverState;
import mb.statix.spec.Rule;
import mb.statix.spec.RuleUtil;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Expands the selected rule.
 */
public final class ExpandRuleStrategy2 implements Strategy2<FocusedSolverState<CUser>, SolverState, SolverContext> {

    private final ITermVar focusVar;

    public ExpandRuleStrategy2(ITermVar focusVar) {
        this.focusVar = focusVar;
    }

    @Override
    public List<SolverState> apply(SolverContext ctx, FocusedSolverState<CUser> state) throws InterruptedException {
        CUser focus = state.getFocus();
        System.out.println("Expand rule: " + focus);

        final ImmutableSet<Rule> rules = ctx.getSpec().rules().getOrderIndependentRules(focus.name());
        SolverState searchState = state.getInnerState();
        List<SolverState> output = RuleUtil.applyAll(searchState.getState(), rules, focus.args(), focus).stream()
            .map(t -> searchState.withApplyResult(t._2(), focus)).collect(Collectors.toList());

        for (SolverState s : output) {
            System.out.println("- " + s.project(focusVar));
        }

        return output;
    }

    @Override
    public String toString() {
        return "expandRule";
    }

}
