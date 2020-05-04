package mb.statix.search.strategies2;

import mb.statix.common.strategies.Strategy2;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;


/**
 * The distinct(s) strategy, which applies s, then returns only the distinct results.
 */
public final class DistinctStrategy2<A, B, CTX> implements Strategy2<A, B, CTX> {

    private final Strategy2<A, B, CTX> strategy;

    public DistinctStrategy2(Strategy2<A, B, CTX> strategy) {
        this.strategy = strategy;
    }

    @Override
    public List<B> apply(CTX ctx, A input) throws InterruptedException {
        List<B> output = this.strategy.apply(ctx, input);
        LinkedHashSet<B> distinctOutput = new LinkedHashSet<>(output);
        return new ArrayList<>(distinctOutput);
    }

    @Override
    public String toString() {
        return "distinct(" + strategy.toString() + ")";
    }

}
