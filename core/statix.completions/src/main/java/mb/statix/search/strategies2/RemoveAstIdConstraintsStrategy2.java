package mb.statix.search.strategies2;

import com.google.common.collect.Maps;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.unification.ud.IUniDisunifier;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.common.strategies.Strategy2;
import mb.statix.constraints.CAstId;
import mb.statix.constraints.CEqual;
import mb.statix.constraints.CResolveQuery;
import mb.statix.generator.scopegraph.DataWF;
import mb.statix.generator.scopegraph.NameResolution;
import mb.statix.generator.strategy.ResolveDataWF;
import mb.statix.scopegraph.reference.*;
import mb.statix.scopegraph.terms.Scope;
import mb.statix.solver.Delay;
import mb.statix.solver.IConstraint;
import mb.statix.solver.IState;
import mb.statix.solver.completeness.ICompleteness;
import mb.statix.solver.query.RegExpLabelWF;
import mb.statix.solver.query.RelationLabelOrder;
import mb.statix.spec.Spec;
import org.metaborg.util.functions.Predicate2;
import sun.util.resources.cldr.en.CalendarData_en_AS;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Removes AST index constraints that could not be solved.
 */
public final class RemoveAstIdConstraintsStrategy2 implements Strategy2<SolverState, SolverState, SolverContext> {

    @Override
    public List<SolverState> apply(SolverContext ctx, SolverState input) throws InterruptedException {
        List<IConstraint> toRemove = input.getConstraints().stream().filter(c -> c instanceof CAstId).collect(Collectors.toList());
        SolverState newState = input.updateConstraints(Collections.emptyList(), toRemove);
        return Collections.singletonList(newState);
    }

    @Override
    public String toString() {
        return "removeAstIdConstraints";
    }

}
