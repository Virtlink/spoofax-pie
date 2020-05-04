package mb.statix.search.strategies2;

import com.google.common.collect.ImmutableList;
import mb.statix.common.strategies.Strategy;
import mb.statix.common.strategies.Strategy2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * The s1; s2; ...; sn strategy.
 */
public final class SeqStrategy2<I, O, CTX> implements Strategy2<I, O, CTX> {

    private final List<Strategy2<?, ?, CTX>> strategies;

    private SeqStrategy2(List<Strategy2<?, ?, CTX>> strategies) {
        this.strategies = strategies;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public List<O> apply(CTX ctx, I input) throws InterruptedException {
        List<Object> values = new ArrayList();
        values.add(input);
        List<Object> newValues = new ArrayList<>();
        for (Strategy2 strategy : this.strategies) {
            for (Object value : values) {
                newValues.addAll(strategy.apply(ctx, value));
            }
            List<Object> tmp = values;
            values = newValues;
            newValues = tmp;
            newValues.clear();
        }
        return (List<O>)values;
    }

    @Override
    public String toString() {
        return this.strategies.stream()
                .map(Object::toString)
                .collect(Collectors.joining("; ", "(", ")"));
    }

    /**
     * A builder for sequences of strategies.
     *
     * @param <I> the input type
     * @param <O> the output type
     */
    public static class Builder<I, O, CTX> {

        private final ImmutableList.Builder<Strategy2<?, ?, CTX>> ss = ImmutableList.builder();

        public Builder(Strategy2<I, O, CTX> s) {
            ss.add(s);
        }

        public <X> Builder<I, X, CTX> $(Strategy2<O, X, CTX> s) {
            ss.add(s);
            //noinspection unchecked
            return (Builder<I, X, CTX>) this;
        }

        public SeqStrategy2<I, O, CTX> $() {
            return new SeqStrategy2<>(ss.build());
        }

    }

}
