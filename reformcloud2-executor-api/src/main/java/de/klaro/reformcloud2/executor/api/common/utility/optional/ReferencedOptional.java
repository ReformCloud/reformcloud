package de.klaro.reformcloud2.executor.api.common.utility.optional;

import com.google.common.annotations.Beta;
import de.klaro.reformcloud2.executor.api.common.utility.annotiations.ForRemoval;
import de.klaro.reformcloud2.executor.api.common.utility.annotiations.Nullable;

import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * @deprecated This class is marked as deprecated because it can get an complete rewrite
 */
@Deprecated
public final class ReferencedOptional<T> implements Serializable {

    private static final long serialVersionUID = 2358039311687874123L;

    /**
     * @deprecated We should use a better than using an optional in an reference here
     * May look like
     * <p>
     *     private final AtomicReference<T> reference = new AtomicReference<>();
     * </p>
     *
     * and update the {@link #update(Object)} method to set the reference if it's not null
     */
    @Deprecated
    @ForRemoval
    private final AtomicReference<Optional<T>> reference = new AtomicReference<>();

    public static <T> ReferencedOptional<T> empty() {
        return new ReferencedOptional<>();
    }

    /**
     * @deprecated We should replace this method with a newer one to remove the suppressWarnings on unchecked assignments
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public static <T> ReferencedOptional<T> build(@Nullable T value) {
        return new ReferencedOptional<>().update(value);
    }

    /**
     * This current method is not using the new code which we want to implement
     *
     * This method should set the reference to the new value not set it to an optional
     */
    @Deprecated
    public ReferencedOptional update(T newValue) {
        reference.set(Optional.ofNullable(newValue));
        return this;
    }

    @Beta
    public T orNothing() {
        return orElse(null);
    }

    /**
     * @deprecated This method do not follow the new code which we want to implement soon
     * @param consumer Magic value
     */
    @Deprecated
    public void ifPresent(Consumer<T> consumer) {
        if (reference.get() != null && reference.get().isPresent()) {
            reference.get().ifPresent(consumer);
        }
    }

    @Deprecated
    public T orElse(T t) {
        if (reference.get() == null || !reference.get().isPresent()) {
            return t;
        }

        return reference.get().get();
    }

    @Deprecated
    public T orThrow(RuntimeException exception) {
        if (reference.get() == null || !reference.get().isPresent()) {
            throw exception;
        }

        return reference.get().get();
    }

    @Deprecated
    @ForRemoval(reason = "We would like to replace the reference containing an Optional with a direct reference (Find a better method here?)")
    public Optional<T> toJava() {
        return reference.get();
    }
}
