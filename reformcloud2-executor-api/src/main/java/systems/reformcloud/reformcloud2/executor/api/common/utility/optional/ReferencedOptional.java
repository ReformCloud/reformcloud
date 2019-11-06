package systems.reformcloud.reformcloud2.executor.api.common.utility.optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public final class ReferencedOptional<T> implements Serializable {

    private static final long serialVersionUID = 2358039311687874123L;

    // =======================

    public static <T> ReferencedOptional<T> empty() {
        return new ReferencedOptional<>();
    }

    @SuppressWarnings("unchecked")
    public static <T> ReferencedOptional<T> build(@Nullable T value) {
        return new ReferencedOptional<>().update(value);
    }

    // =======================

    private final AtomicReference<T> reference = new AtomicReference<>();

    @Nonnull
    public ReferencedOptional update(@Nullable T newValue) {
        if (newValue != null) {
            reference.set(newValue);
        }

        return this;
    }

    @Nullable
    public T orNothing() {
        return orElse(null);
    }

    public void ifPresent(@Nonnull Consumer<T> consumer) {
        T value = reference.get();
        if (value != null) {
            consumer.accept(value);
        }
    }

    @Nullable
    public T orElse(@Nullable T t) {
        T value = reference.get();
        if (value == null) {
            return t;
        }

        return value;
    }

    public boolean isPresent() {
        return get() != null;
    }

    @Nullable
    public T get() {
        return reference.get();
    }
}
