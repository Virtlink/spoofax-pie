package mb.statix.search.strategies2;

import mb.statix.common.strategies.Strategy;
import mb.statix.common.strategies.Strategy2;
import mb.statix.search.StreamUtils;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;


/**
 * The debug(s, a) strategy wraps a strategy, evaluates it, and applies an action to each element.
 */
public final class DebugStrategy2<I, O, CTX> implements Strategy2<I, O, CTX> {

    private final Consumer<O> action;
    private final Strategy2<I, O, CTX> strategy;

    public DebugStrategy2(Strategy2<I, O, CTX> strategy, Consumer<O> action) {
        this.strategy = strategy;
        this.action = action;
    }

    @Override
    public List<O> apply(CTX ctx, I input) throws InterruptedException {
        // This buffers the entire stream.
        // This has a performance implication, but is required for a better debugging experience.
        List<O> output = this.strategy.apply(ctx, input);
        output.forEach(this.action);
        return output;
    }

    @Override
    public String toString() {
        return "debug(" + strategy.toString() + ")";
    }

}
