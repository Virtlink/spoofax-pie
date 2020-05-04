package mb.statix.search.strategies2;

import mb.statix.common.strategies.Strategy2;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * The if(c, t, e) strategy, which applies c then t if c succeeds; otherwise, e.
 */
public final class IfStrategy2<I, M, O, CTX> implements Strategy2<I, O, CTX> {

    private final Strategy2<I, M, CTX> c;
    private final Strategy2<M, O, CTX> t;
    private final Strategy2<I, O, CTX> e;

    public IfStrategy2(Strategy2<I, M, CTX> c, Strategy2<M, O, CTX> t, Strategy2<I, O, CTX> e) {
        this.c = c;
        this.t = t;
        this.e = e;
    }

    @Override
    public List<O> apply(CTX ctx, I input) throws InterruptedException {
        List<M> output = this.c.apply(ctx, input);
        if (output.isEmpty()) {
            return this.e.apply(ctx, input);
        } else {
            return output.stream().flatMap(o -> {
                try {
                    return this.t.apply(ctx, o).stream();
                } catch(InterruptedException interruptedException) {
                    throw new RuntimeException(interruptedException);
                }
            }).collect(Collectors.toList());
        }
    }

    @Override
    public String toString() {
        return "if(" + c.toString() + ", " + t.toString() + ", " + e.toString() + ")";
    }

}
