package mb.statix.search.strategies;

import mb.statix.common.strategies.Strategy;
import one.util.streamex.StreamEx;

import java.util.function.Predicate;
import java.util.stream.Stream;


/**
 * The assert(c) strategy, which tests a condition and fails when the condition fails.
 */
public final class AssertStrategy<T, CTX> implements Strategy<T, T, CTX> {

    private final Predicate<T> condition;

    public AssertStrategy(Predicate<T> condition) {
        this.condition = condition;
    }

    @Override
    public Stream<T> apply(CTX ctx, T input) throws InterruptedException {
        if (!condition.test(input)) return Stream.empty();
        return Stream.of(input);

    }

    @Override
    public String toString() {
        return "assert(" + condition.toString() + ")";
    }

}
