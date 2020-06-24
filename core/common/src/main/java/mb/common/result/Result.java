package mb.common.result;

import mb.common.option.Option;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A type that is either a value of type {@link T}, or an error of type {@link E}.
 *
 * @param <T> Type of values.
 * @param <E> Type of errors, descendants of {@link Exception}.
 * @apiNote Only {@link Serializable} when {@link T} and {@link E} are {@link Serializable}.
 */
public interface Result<T, E extends Exception> extends Serializable {
    static <T, E extends Exception> Result<T, E> ofOk(T value) {
        return new Ok<>(value);
    }

    static <T, E extends Exception> Result<T, E> ofNullableOrElse(@Nullable T value, Supplier<? extends E> def) {
        if(value != null) {
            return new Ok<>(value);
        } else {
            return new Err<>(def.get());
        }
    }

    static <T, E extends Exception> Result<T, E> ofErr(E error) {
        return new Err<>(error);
    }


    boolean isOk();

    Option<T> ok();

    default void ifOk(Consumer<? super T> consumer) {
        ok().ifSome(consumer);
    }


    boolean isErr();

    Option<E> err();

    default void ifErr(Consumer<? super E> consumer) {
        err().ifSome(consumer);
    }

    default void throwIfError() throws E {
        if(isErr()) {
            // noinspection ConstantConditions (`getErr` is safe because error is present if `isErr` returns true)
            throw getErr();
        }
    }

    default void throwUncheckedIfError() {
        if(isErr()) {
            // `getErr` is safe because error is present if `isErr` returns true.
            throw new RuntimeException(getErr());
        }
    }


    default void ifElse(Consumer<? super T> okConsumer, Consumer<? super E> errConsumer) {
        if(isOk()) {
            okConsumer.accept(get());
        } else {
            errConsumer.accept(getErr());
        }
    }

    default void ifElse(Consumer<? super T> okConsumer, Runnable errRunner) {
        if(isOk()) {
            okConsumer.accept(get());
        } else {
            errRunner.run();
        }
    }


    default <U> Result<U, E> map(Function<? super T, ? extends U> mapper) {
        if(isOk()) {
            return Result.ofOk(mapper.apply(get()));
        } else {
            // noinspection unchecked (cast is safe because it is impossible to get a value of type U in the err case)
            return (Result<U, E>)this;
        }
    }

    default <U, F extends Exception> Result<U, E> mapThrowing(ExceptionalFunction<? super T, ? extends U, F> mapper) throws F {
        if(isOk()) {
            //noinspection ConstantConditions (`get` is safe because value is present if `isOk` returns true)
            return Result.ofOk(mapper.apply(get()));
        } else {
            // noinspection unchecked (cast is safe because it is impossible to get a value of type U in the err case)
            return (Result<U, E>)this;
        }
    }

    default <U> Result<U, ?> mapCatching(ExceptionalFunction<? super T, ? extends U, ?> mapper) {
        if(isOk()) {
            try {
                //noinspection ConstantConditions (`get` is safe because value is present if `isOk` returns true)
                return Result.ofOk(mapper.apply(get()));
            } catch(Exception e) {
                return Result.ofErr(e);
            }
        } else {
            // noinspection unchecked (cast is safe because it is impossible to get a value of type U in the err case)
            return (Result<U, ?>)this;
        }
    }

    default <U, F extends Exception> Result<U, F> mapCatching(ExceptionalFunction<? super T, ? extends U, F> mapper, Class<F> exceptionClass) {
        if(isOk()) {
            try {
                //noinspection ConstantConditions (`get` is safe because value is present if `isOk` returns true)
                return Result.ofOk(mapper.apply(get()));
            } catch(Exception e) {
                if(e.getClass().equals(exceptionClass)) {
                    // noinspection unchecked (cast is safe because `e`'s class is equal to `exceptionClass`)
                    return Result.ofErr((F)e);
                } else {
                    // `e` is not of type `F`, and it cannot be another checked exception. Therefore, it is either a
                    // `RuntimeException` or a checked exception that is sneakily thrown. In either case, it is safe to
                    // sneakily rethrow the exception
                    SneakyThrow.doThrow(e);
                    // Because `SneakyThrow.doThrow` throws, the following statement will never be executed, but it is
                    // still needed to make the Java compiler happy.
                    throw new RuntimeException(e);
                }
            }
        } else {
            // noinspection unchecked (cast is safe because it is impossible to get a value of type U in the err case)
            return (Result<U, F>)this;
        }
    }

    default <U> U mapOr(Function<? super T, ? extends U> mapper, U def) {
        return ok().mapOr(mapper, def);
    }

    default <U> @Nullable U mapOrNull(Function<? super T, ? extends U> mapper) {
        return ok().mapOrNull(mapper);
    }

    default <U> U mapOrThrow(Function<? super T, ? extends U> mapper) throws E {
        // `getErr` is safe because error is present if not ok case.
        return ok().mapOrElseThrow(mapper, this::getErr);
    }

    default <U> U mapOrElse(
        Function<? super T, ? extends U> mapper,
        Supplier<? extends U> def
    ) {
        return ok().mapOrElse(mapper, def);
    }

    default <U> U mapOrElse(
        Function<? super T, ? extends U> mapper,
        Function<? super E, ? extends U> def
    ) {
        // `getErr` is safe because error is present if not ok case.
        return ok().mapOrElse(mapper, () -> def.apply(getErr()));
    }

    default <U, F extends Exception> U mapOrElseThrow(
        Function<? super T, ? extends U> mapper,
        Function<? super E, ? extends F> def
    ) throws F {
        // `getErr` is safe because error is present if not ok case.
        return ok().mapOrElseThrow(mapper, () -> def.apply(getErr()));
    }


    default <F extends Exception> Result<T, F> mapErr(Function<? super E, ? extends F> mapper) {
        // noinspection unchecked (cast is safe because it is impossible to get a value of type F in the ok case)
        return err()
            .map(e -> Result.<T, F>ofErr(mapper.apply(e)))
            .unwrapOrElse(() -> (Result<T, F>)this);
    }

    default <F extends Exception> F mapErrOr(Function<? super E, ? extends F> mapper, F def) {
        return err().mapOr(mapper, def);
    }

    default <F extends Exception> @Nullable F mapErrOrNull(Function<? super E, ? extends F> mapper) {
        return err().mapOrNull(mapper);
    }

    default <F extends Exception> F mapErrOrElse(
        Function<? super E, ? extends F> mapper,
        Supplier<? extends F> def
    ) {
        return err().mapOrElse(mapper, def);
    }

    default <F extends Exception> F mapErrOrElse(
        Function<? super E, ? extends F> mapper,
        Function<? super T, ? extends F> def
    ) {
        // `get` is safe because value is present if not err case.
        return err().mapOrElse(mapper, () -> def.apply(get()));
    }


    default <U> Result<U, E> flatMap(Function<? super T, ? extends Result<U, E>> mapper) {
        // noinspection unchecked (cast is safe because it is impossible to get a value of type U in the err case)
        return ok().mapOrElse(mapper, () -> (Result<U, E>)this);
    }

    default <U, F extends Exception> Result<U, F> flatMapOrElse(
        Function<? super T, ? extends Result<U, F>> okMapper,
        Function<? super E, ? extends Result<U, F>> errMapper
    ) {
        // `getErr` is safe because error is present if not ok case.
        return ok().mapOrElse(okMapper, () -> errMapper.apply(getErr()));
    }


    default <U> Result<U, E> and(Result<U, E> other) {
        if(isErr()) {
            // noinspection unchecked (cast is safe because it is impossible to get a value of type U in the err case)
            return (Result<U, E>)this;
        }
        return other;
    }

    default <F extends Exception> Result<T, F> or(Result<T, F> other) {
        if(isOk()) {
            // noinspection unchecked (cast is safe because it is impossible to get a value of type F in the ok case)
            return (Result<T, F>)this;
        }
        return other;
    }


    default T unwrap() throws E {
        // get is safe because error is present if not ok case
        return ok().unwrapOrElseThrow(() -> err().get());
    }

    default T unwrapUnchecked() {
        // get is safe because error is present if not ok case
        return ok().unwrapOrElseThrow(() -> new RuntimeException(err().get()));
    }

    default T unwrapOr(T def) {
        return ok().unwrapOr(def);
    }

    default T unwrapOrElse(Supplier<? extends T> def) {
        return ok().unwrapOrElse(def);
    }


    default E unwrapErr() {
        return err().unwrapOrElseThrow(() -> new RuntimeException("Called `unwrapErr` on an `Ok` result"));
    }

    default E unwrapErrOr(E def) {
        return err().unwrapOr(def);
    }

    default E unwrapErrOrElse(Supplier<? extends E> def) {
        return err().unwrapOrElse(def);
    }


    default @Nullable T get() {
        return ok().get();
    }

    default @Nullable T getOr(@Nullable T def) {
        return ok().getOr(def);
    }

    default @Nullable T getOrElse(Supplier<? extends @Nullable T> def) {
        return ok().getOrElse(def);
    }


    default @Nullable E getErr() {
        return err().get();
    }

    default @Nullable E getErrOr(@Nullable E def) {
        return err().getOr(def);
    }

    default @Nullable E getErrOrElse(Supplier<? extends @Nullable E> def) {
        return err().getOrElse(def);
    }


    class Ok<T, E extends Exception> implements Result<T, E>, Serializable {
        public final T value;

        public Ok(T value) {
            this.value = value;
        }


        @Override public boolean isOk() {
            return true;
        }

        @Override public Option<T> ok() {
            return Option.ofSome(value);
        }

        @Override public boolean isErr() {
            return false;
        }

        @Override public Option<E> err() {
            return Option.ofNone();
        }


        @Override public boolean equals(Object o) {
            if(this == o) return true;
            if(o == null || getClass() != o.getClass()) return false;
            final Ok<?, ?> ok = (Ok<?, ?>)o;
            return value.equals(ok.value);
        }

        @Override public int hashCode() {
            return value.hashCode();
        }

        @Override public String toString() {
            return "Ok(" + value + ")";
        }
    }

    class Err<T, E extends Exception> implements Result<T, E>, Serializable {
        public final E error;

        public Err(E error) {
            this.error = error;
        }


        @Override public boolean isOk() {
            return false;
        }

        @Override public Option<T> ok() {
            return Option.ofNone();
        }

        @Override public boolean isErr() {
            return true;
        }

        @Override public Option<E> err() {
            return Option.ofSome(error);
        }


        @Override public boolean equals(Object o) {
            if(this == o) return true;
            if(o == null || getClass() != o.getClass()) return false;
            final Err<?, ?> err = (Err<?, ?>)o;
            return error.equals(err.error);
        }

        @Override public int hashCode() {
            return error.hashCode();
        }

        @Override public String toString() {
            return "Err(" + error + ")";
        }
    }
}