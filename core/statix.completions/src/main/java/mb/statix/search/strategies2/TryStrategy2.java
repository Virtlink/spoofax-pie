package mb.statix.search.strategies2;

import mb.statix.common.strategies.Strategy;
import mb.statix.common.strategies.Strategy2;
import one.util.streamex.StreamEx;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;


/**
 * The try(s) strategy, which applies s but always succeeds.
 */
public final class TryStrategy2<T, CTX> implements Strategy2<T, T, CTX> {

    private final Strategy2<T, T, CTX> s;

    public TryStrategy2(Strategy2<T, T, CTX> s) {
        this.s = s;
    }

    @Override
    public List<T> apply(CTX ctx, T input) throws InterruptedException {

        List<T> output = this.s.apply(ctx, input);
        if (output.isEmpty()) {
            return Collections.singletonList(input);
        } else {
            return output;
        }
    }

    @Override
    public String toString() {
        return "try(" + s.toString() + ")";
    }

}
