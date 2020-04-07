package systems.reformcloud.reformcloud2.executor.api.common.utility.optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Represents an extended optional for deeper use
 *
 * @param <T> The type of the object stored in the optional
 */
public final class ReferencedOptional<T> implements Serializable {

    private static final long serialVersionUID = 2358039311687874123L;

    // =======================

    @NotNull
    public static <T> ReferencedOptional<T> empty() {
        return new ReferencedOptional<>();
    }

    @NotNull
    public static <T> ReferencedOptional<T> build(@Nullable T value) {
        return new ReferencedOptional<T>().update(value);
    }

    // =======================

    private final AtomicReference<T> reference = new AtomicReference<>();

    @NotNull
    public ReferencedOptional<T> update(@Nullable T newValue) {
        if (newValue != null) {
            reference.set(newValue);
        }

        return this;
    }

    @Nullable
    public T orNothing() {
        return orElse(null);
    }

    public ReferencedOptional<T> ifPresent(@NotNull Consumer<T> consumer) {
        T value = reference.get();
        if (value != null) {
            consumer.accept(value);
        }

        return this;
    }

    public ReferencedOptional<T> ifEmpty(@NotNull Consumer<Void> consumer) {
        if (isEmpty()) {
            consumer.accept(null);
        }

        return this;
    }

    @Nullable
    public T orElse(@Nullable T t) {
        T value = reference.get();
        if (value == null) {
            return t;
        }

        return value;
    }

    public void orElseDo(@NotNull Predicate<T> predicate, @NotNull Runnable ifFalse, @NotNull Consumer<T> or) {
        T value = reference.get();
        if (!predicate.test(value)) {
            ifFalse.run();
            return;
        }

        or.accept(value);
    }

    public boolean isPresent() {
        return get() != null;
    }

    public boolean isEmpty() {
        return get() == null;
    }

    public T get() {
        return reference.get();
    }
}
