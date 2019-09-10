package de.klaro.reformcloud2.executor.api.common.utility.optional;

import de.klaro.reformcloud2.executor.api.common.utility.annotiations.Nullable;

import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public final class ReferencedOptional<T> implements Serializable {

    private static final long serialVersionUID = 2358039311687874123L;

    private final AtomicReference<Optional<T>> reference = new AtomicReference<>();

    public static <T> ReferencedOptional<T> empty() {
        return new ReferencedOptional<>();
    }

    @SuppressWarnings("unchecked")
    public static <T> ReferencedOptional<T> build(@Nullable T value) {
        return new ReferencedOptional<>().update(value);
    }

    public ReferencedOptional update(T newValue) {
        reference.set(Optional.ofNullable(newValue));
        return this;
    }

    public T orNothing() {
        if (reference.get() == null || !reference.get().isPresent()) {
            return null;
        }

        return reference.get().get();
    }

    public T orGet(T t) {
        if (reference.get() == null || !reference.get().isPresent()) {
            return t;
        }

        return reference.get().get();
    }

    public T orThrow(RuntimeException exception) {
        if (reference.get() == null || !reference.get().isPresent()) {
            throw exception;
        }

        return reference.get().get();
    }
}
