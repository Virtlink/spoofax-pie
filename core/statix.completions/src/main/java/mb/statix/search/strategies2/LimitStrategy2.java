package mb.statix.search.strategies2;

import mb.statix.common.strategies.Strategy;
import mb.statix.common.strategies.Strategy2;

import java.util.List;
import java.util.stream.Stream;


/**
 * The limit(i, s) strategy, which applies s, then returns only the i first results.
 */
public final class LimitStrategy2<A, B, CTX> implements Strategy2<A, B, CTX> {

    private final int limit;
    private final Strategy2<A, B, CTX> strategy;

    public LimitStrategy2(int limit, Strategy2<A, B, CTX> strategy) {
        this.limit = limit;
        this.strategy = strategy;
    }

    @Override
    public List<B> apply(CTX ctx, A input) throws InterruptedException {
        List<B> output = this.strategy.apply(ctx, input);
        return output.subList(0, Math.min(limit, output.size()));
    }

    @Override
    public String toString() {
        return "limit(" + limit + ", " + strategy.toString() + ")";
    }

}
