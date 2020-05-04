package mb.statix.search.strategies2;

import mb.statix.common.strategies.Strategy2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static mb.statix.search.strategies2.Strategies2.repeat;
import static mb.statix.search.strategies2.Strategies2.seq;
import static mb.statix.search.strategies2.Strategies2.try_;


/**
 * The fix(s) strategy, which applies s until the set of results no longer changes;
 * or until it fails.
 */
public final class FixSetStrategy2<T, CTX> implements Strategy2<T, T, CTX> {

    private final Strategy2<T, T, CTX> s;

    public FixSetStrategy2(Strategy2<T, T, CTX> s) {
        this.s = s;
    }

    @Override
    public List<T> apply(CTX ctx, T input) throws InterruptedException {
        System.out.println("Fixset: start");

        Set<T> values = new LinkedHashSet<T>();
        values.add(input);
        Set<T> newValues = new LinkedHashSet<T>();

        while (true) {
            for(T value : values) {
                newValues.addAll(s.apply(ctx, value));
            }

            // Everything failed, we return
            if (newValues.isEmpty()) {
                System.out.println("Fixset: return on fail");
                return new ArrayList<>(values);
            }

            // Everything stayed the same, we return
            if (values.equals(newValues)) {
                System.out.println("Fixset: return on fixpoint");
                return new ArrayList<>(newValues);
            }

            Set<T> tmp = values;
            values = newValues;
            newValues = tmp;
            newValues.clear();
        }
    }

    @Override
    public String toString() {
        return "repeat(" + s.toString() + ")";
    }

}
