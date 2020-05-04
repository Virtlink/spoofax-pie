package mb.statix.search.strategies2;

import mb.statix.common.strategies.Strategy;
import mb.statix.common.strategies.Strategy2;
import one.util.streamex.StreamEx;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * The non-deterministic or() strategy, which splits the search tree.
 */
public final class OrStrategy2<I, O, CTX> implements Strategy2<I, O, CTX> {

    private final Strategy2<I, O, CTX> strategy1;
    private final Strategy2<I, O, CTX> strategy2;

    public OrStrategy2(Strategy2<I, O, CTX> strategy1, Strategy2<I, O, CTX> strategy2) {
        this.strategy1 = strategy1;
        this.strategy2 = strategy2;
    }

    @Override
    public List<O> apply(CTX ctx, I input) throws InterruptedException {
        return StreamEx.of(this.strategy1.apply(ctx, input)).append(this.strategy2.apply(ctx, input)).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return strategy1.toString() + " + " + strategy2.toString();
    }

}
