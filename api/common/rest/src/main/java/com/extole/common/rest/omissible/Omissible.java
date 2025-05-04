package com.extole.common.rest.omissible;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.extole.common.lang.ThrowingConsumer;
import com.extole.common.lang.ToString;

@JsonDeserialize(using = OmissibleDeserializer.class)
public final class Omissible<TYPE> {

    private static final Omissible<?> OMITTED = new Omissible<>(null, true);
    private static final Omissible<?> NULLIFIED = new Omissible<>(null, false);

    private final boolean omitted;

    public static <T> Omissible<T> of(T value) {
        return of(value, false);
    }

    public static <T> Omissible<T> omitted() {
        return (Omissible<T>) OMITTED;
    }

    public static <T> Omissible<T> nullified() {
        return (Omissible<T>) NULLIFIED;
    }

    private final TYPE value;

    private Omissible(TYPE value, boolean omitted) {
        this.value = value;
        this.omitted = omitted;
    }

    @JsonCreator(mode = JsonCreator.Mode.DISABLED)
    static <T> Omissible<T> of(@Nullable T value, boolean omitted) {
        return new Omissible<>(value, omitted);
    }

    public boolean isOmitted() {
        return omitted;
    }

    public boolean isPresent() {
        return !omitted;
    }

    @JsonValue
    @Nullable
    public TYPE getValue() {
        return value;
    }

    public <EXCEPTION extends Exception> void ifPresent(ThrowingConsumer<TYPE, EXCEPTION> action) throws EXCEPTION {
        if (omitted) {
            return;
        }
        action.accept(value);
    }

    @Nullable
    public TYPE orElse(TYPE other) {
        return omitted ? other : value;
    }

    @Nullable
    public TYPE orElseGet(Supplier<? extends TYPE> supplier) {
        return omitted ? supplier.get() : value;
    }

    @Nullable
    public <THROWABLE extends Throwable> TYPE orElseThrow(Supplier<? extends THROWABLE> exceptionSupplier)
        throws THROWABLE {
        if (omitted) {
            throw exceptionSupplier.get();
        } else {
            return value;
        }
    }

    public Omissible<TYPE> filter(Predicate<? super TYPE> predicate) {
        if (omitted) {
            return this;
        } else {
            return predicate.test(value) ? this : omitted();
        }
    }

    public <U> Omissible<U> map(Function<? super TYPE, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (omitted) {
            return Omissible.omitted();
        } else {
            return Omissible.of(mapper.apply(value));
        }
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
