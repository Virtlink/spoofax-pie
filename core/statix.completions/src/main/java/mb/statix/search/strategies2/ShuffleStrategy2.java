package mb.statix.search.strategies2;

import mb.statix.common.strategies.Strategy;
import mb.statix.common.strategies.Strategy2;
import mb.statix.search.RandomUtils;
import mb.statix.search.StreamUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;


/**
 * The shuffle(rng, s) strategy, which applies s, then shuffles the results.
 */
public final class ShuffleStrategy2<A, B, CTX> implements Strategy2<A, B, CTX> {

    private final Random rng;
    private final Strategy2<A, B, CTX> strategy;

    public ShuffleStrategy2(Random rng, Strategy2<A, B, CTX> strategy) {
        this.rng = rng;
        this.strategy = strategy;
    }

    @Override
    public List<B> apply(CTX ctx, A input) throws InterruptedException {
        List<B> outputs = this.strategy.apply(ctx, input);
        Collections.shuffle(outputs, this.rng);
        return outputs;
    }

    @Override
    public String toString() {
        return "shuffle(" + RandomUtils.getSeed(rng) + ", " + strategy.toString() + ")";
    }

}
