package mb.statix.common.strategies;

import java.util.List;
import java.util.stream.Stream;


/**
 * A strategy, which takes an input and produces a list of outputs.
 */
@FunctionalInterface
public interface Strategy2<I, O, CTX> {

    /**
     * Applies the search strategy.
     *
     * @param ctx the search context
     * @param input the input
     * @return a list of results; or an empty list when the strategy failed
     * @throws InterruptedException the operation was interrupted
     */
    List<O> apply(CTX ctx, I input) throws InterruptedException;

}
