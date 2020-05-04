package mb.statix.search.strategies2;

import mb.statix.common.strategies.Strategy;
import mb.statix.common.strategies.Strategy2;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;


/**
 * The id() strategy, which always succeeds.
 */
public final class IdStrategy2<T, CTX> implements Strategy2<T, T, CTX> {

    @Override
    public List<T> apply(CTX ctx, T input) throws InterruptedException {
        return Collections.singletonList(input);
    }

    @Override
    public String toString() {
        return "id";
    }

}
