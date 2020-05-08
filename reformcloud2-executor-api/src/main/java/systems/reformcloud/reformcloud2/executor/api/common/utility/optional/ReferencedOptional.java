/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.executor.api.common.utility.optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
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

    @NotNull
    public <V> ReferencedOptional<V> map(@NotNull Function<T, V> mapper) {
        if (this.isEmpty()) {
            return ReferencedOptional.empty();
        }

        return ReferencedOptional.build(mapper.apply(this.reference.get()));
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
