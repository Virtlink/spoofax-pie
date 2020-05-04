package mb.statix.search.strategies2;

import mb.statix.common.strategies.Strategy;
import mb.statix.common.strategies.Strategy2;

import java.util.List;
import java.util.stream.Stream;

import static mb.statix.search.strategies2.Strategies2.repeat;
import static mb.statix.search.strategies2.Strategies2.seq;
import static mb.statix.search.strategies2.Strategies2.try_;


/**
 * The repeat(s) strategy, which applies s until it fails.
 */
public final class RepeatStrategy2<T, CTX> implements Strategy2<T, T, CTX> {

    private final Strategy2<T, T, CTX> s;

    public RepeatStrategy2(Strategy2<T, T, CTX> s) {
        this.s = s;
    }

    @Override
    public List<T> apply(CTX ctx, T input) throws InterruptedException {
        return try_(seq(s).$(repeat(s)).$()).apply(ctx, input);
    }

    @Override
    public String toString() {
        return "repeat(" + s.toString() + ")";
    }

}
