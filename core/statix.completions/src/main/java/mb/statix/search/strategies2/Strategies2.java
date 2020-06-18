package mb.statix.search.strategies2;

import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.common.strategies.Strategy;
import mb.statix.common.strategies.Strategy2;
import mb.statix.search.strategies.AssertStrategy;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;


/**
 * Convenience functions for creating strategies.
 */
public final class Strategies2 {

    /**
     * Asserts that the given condition is true.
     *
     * @param condition the condition
     * @param <T> the type of input and outputs for the strategy
     * @param <CTX> the context of the strategy
     * @return the resulting strategy
     */
    public static <T, CTX> AssertStrategy<T, CTX> assertThat(Predicate<T> condition) {
        return new AssertStrategy<>(condition);
    }

    /**
     * Performs an action on all results of the given strategy.
     *
     * @param s the strategy
     * @param action the action to perform
     * @param <T> the type of input for the strategy
     * @param <R> the type of outputs for the strategy
     * @param <CTX> the context of the strategy
     * @return the resulting strategy
     */
    public static <T, R, CTX> DebugStrategy2<T, R, CTX> debug(Strategy2<T, R, CTX> s, Consumer<R> action) {
        return new DebugStrategy2<>(s, action);
    }

    /**
     * Returns only the distinct results.
     *
     * @param s the strategy
     * @param <T> the type of input for the strategy
     * @param <R> the type of outputs for the strategy
     * @param <CTX> the context of the strategy
     * @return the resulting strategy
     */
    public static <T, R, CTX> DistinctStrategy2<T, R, CTX> distinct(Strategy2<T, R, CTX> s) {
        return new DistinctStrategy2<>(s);
    }

    /**
     * Always fails.
     *
     * @param <T> the type of inputs and outputs for the strategy
     * @param <CTX> the context of the strategy
     * @return the resulting strategy
     */
    public static <T, CTX> FailStrategy2<T, CTX> fail() {
        return new FailStrategy2<>();
    }

    /**
     * Fix-point.
     *
     * @param s the strategy
     * @param <T> the type of inputs and outputs for the strategy
     * @param <CTX> the context of the strategy
     * @return the resulting strategy
     */
    public static <T, CTX> FixSetStrategy2<T, CTX> fixSet(Strategy2<T, T, CTX> s) {
        return new FixSetStrategy2<>(s);
    }

    /**
     * Identity strategy.
     *
     * @param <T> the type of input and outputs for the strategy
     * @param <CTX> the context of the strategy
     * @return the resulting strategy
     */
    public static <T, CTX> IdStrategy2<T, CTX> id() {
        return new IdStrategy2<>();
    }

    /**
     * If strategy.
     *
     * @param c condition
     * @param t then
     * @param e else
     * @param <I> the type of input for the strategy
     * @param <M> the intermediate type
     * @param <O> the type of outputs for the strategy
     * @param <CTX> the context of the strategy
     * @return the resulting strategy
     */
    public static <I, M, O, CTX> IfStrategy2<I, M, O, CTX> if_(Strategy2<I, M, CTX> c, Strategy2<M, O, CTX> t, Strategy2<I, O, CTX> e) {
        return new IfStrategy2<>(c, t, e);
    }

    /**
     * Limits the number of results of the given search strategy.
     *
     * @param limit the maximum number of results
     * @param s the strategy
     * @param <T> the type of input for the strategy
     * @param <R> the type of outputs for the strategy
     * @param <CTX> the context of the strategy
     * @return the resulting strategy
     */
    public static <T, R, CTX> LimitStrategy2<T, R, CTX> limit(int limit, Strategy2<T, R, CTX> s) {
        return new LimitStrategy2<>(limit, s);
    }

    /**
     * Applies two strategies non-deterministically.
     *
     * @param s1 the first strategy
     * @param s2 the second strategy
     * @param <T> the type of input for the strategy
     * @param <R> the type of outputs for the strategy
     * @param <CTX> the context of the strategy
     * @return the resulting strategy
     */
    public static <T, R, CTX> OrStrategy2<T, R, CTX> or(Strategy2<T, R, CTX> s1, Strategy2<T, R, CTX> s2) {
        return new OrStrategy2<>(s1, s2);
    }

    /**
     * Prints all values resulting from the given strategy.
     *
     * @param s the strategy
     * @param <T> the type of input for the strategy
     * @param <R> the type of outputs for the strategy
     * @param <CTX> the context of the strategy
     * @return the resulting strategy
     */
    public static <T, R, CTX> DebugStrategy2<T, R, CTX> print(String prefix, Strategy2<T, R, CTX> s) {
        return new DebugStrategy2<>(s, v -> System.out.println(prefix + v.toString()));
    }

    /**
     * Prints all values resulting from the given strategy.
     *
     * @param s the strategy
     * @param <T> the type of input for the strategy
     * @param <R> the type of outputs for the strategy
     * @param <CTX> the context of the strategy
     * @return the resulting strategy
     */
    public static <T, R, CTX> DebugStrategy2<T, R, CTX> print(Strategy2<T, R, CTX> s) {
        return print("", s);
    }

    /**
     * Prints the value.
     *
     * @param <T> the type of input and outputs for the strategy
     * @param <CTX> the context of the strategy
     * @return the resulting strategy
     */
    public static <T, CTX> DebugStrategy2<T, T, CTX> print() {
        return print(id());
    }

    /**
     * Repeatedly applies a strategy to the results until the strategy fails.
     *
     * @param s the strategy
     * @param <T> the type of input and outputs for the strategy
     * @param <CTX> the context of the strategy
     * @return the resulting strategy
     */
    public static <T, CTX> RepeatStrategy2<T, CTX> repeat(Strategy2<T, T, CTX> s) {
        return new RepeatStrategy2<>(s);
    }

    /**
     * Starts a sequence of strategies to apply.
     *
     * @param s the first strategy to apply
     * @param <T> the type of input for the strategy
     * @param <R> the type of outputs for the strategy
     * @param <CTX> the context of the strategy
     * @return the resulting strategy
     */
    public static <T, R, CTX> SeqStrategy2.Builder<T, R, CTX> seq(Strategy2<T, R, CTX> s) {
        return new SeqStrategy2.Builder<>(s);
    }

    /**
     * Shuffles the results of the given strategy.
     *
     * @param rng the random number generator to use
     * @param s the strategy
     * @param <T> the type of input for the strategy
     * @param <R> the type of outputs for the strategy
     * @param <CTX> the context of the strategy
     * @return the resulting strategy
     */
    public static <T, R, CTX> ShuffleStrategy2<T, R, CTX> shuffle(Random rng, Strategy2<T, R, CTX> s) {
        return new ShuffleStrategy2<>(rng, s);
    }

    /**
     * Shuffles the results of the given strategy with a new random number generator.
     *
     * @param s the strategy
     * @param <T> the type of input for the strategy
     * @param <R> the type of outputs for the strategy
     * @param <CTX> the context of the strategy
     * @return the resulting strategy
     */
    public static <T, R, CTX> ShuffleStrategy2<T, R, CTX> shuffle(Strategy2<T, R, CTX> s) {
        return shuffle(new Random(), s);
    }

    /**
     * Attempts to apply the given strategy on the input.
     *
     * @param s the strategy
     * @param <T> the type of input and outputs for the strategy
     * @param <CTX> the context of the strategy
     * @return the resulting strategy
     */
    public static <T, CTX> TryStrategy2<T, CTX> try_(Strategy2<T, T, CTX> s) {
        return new TryStrategy2<>(s);
    }

}
