package mb.statix.search.strategies2;

import mb.statix.common.strategies.Strategy;
import mb.statix.common.strategies.Strategy2;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;


/**
 * The fail() strategy, which always fails.
 */
public final class FailStrategy2<T, CTX> implements Strategy2<T, T, CTX> {

    @Override
    public List<T> apply(CTX ctx, T input) throws InterruptedException {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "fail";
    }

}
