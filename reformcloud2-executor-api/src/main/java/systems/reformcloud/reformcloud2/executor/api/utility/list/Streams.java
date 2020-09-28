/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.reformcloud.reformcloud2.executor.api.utility.list;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.utility.optional.ReferencedOptional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@ApiStatus.Internal
public final class Streams {

    private Streams() {
        throw new UnsupportedOperationException();
    }

    /**
     * Makes all strings in the list lower-case
     *
     * @param list The input list
     * @return A new list with all keys of the given list, but lower-case
     */
    @NotNull
    public static List<String> toLowerCase(@NotNull Collection<String> list) {
        List<String> strings = new ArrayList<>();
        for (String s : list) {
            strings.add(s.toLowerCase());
        }

        return strings;
    }

    /**
     * Creates a new list with all given parameters
     *
     * @param in  The given keys which should be in the new list
     * @param <T> The object parameter of the values in the list
     * @return A new list with all values of the input list in it
     */
    @NotNull
    public static <T> List<T> newList(@NotNull Collection<T> in) {
        return new ArrayList<>(in);
    }

    /**
     * Copies a sorted set including the {@link Comparator} and all values in it
     *
     * @param set The incoming set with all values in it
     * @param <T> The object parameter of the values in the list
     * @return A copy of the current set
     */
    @NotNull
    public static <T> SortedSet<T> copySortedSet(@NotNull SortedSet<T> set) {
        SortedSet<T> sortedSet = new TreeSet<>(set.comparator());
        sortedSet.addAll(set);
        return sortedSet;
    }

    /**
     * Applies a function to all values in the given list
     *
     * @param in       The incoming list with the key values in it
     * @param function The function which should get applied to the values
     * @param <T>      The object parameter of the values in the list
     * @param <F>      The object parameter of the values in the outgoing list
     * @return A new list with all values of the incoming list, applied to the function
     */
    @NotNull
    public static <T, F> List<F> apply(@NotNull List<T> in, @NotNull Function<T, F> function) {
        List<F> out = new ArrayList<>();
        for (T t : in) {
            out.add(function.apply(t));
        }

        return out;
    }

    /**
     * Filters a specific value out of the given list
     *
     * @param in        The incoming list
     * @param predicate The predicate which checks if the current object equals the the filter
     * @param <T>       The object parameter of the values in the list
     * @return The first value in the list which equals to the filter
     */
    @Nullable
    public static <T> T filter(@NotNull Collection<T> in, @NotNull Predicate<T> predicate) {
        for (T t : in) {
            if (predicate.test(t)) {
                return t;
            }
        }

        return null;
    }

    /**
     * Filters a specific value out of the given list
     *
     * @param in        The incoming list
     * @param predicate The predicate which checks if the current object equals the the filter
     * @param <T>       The object parameter of the values in the list
     * @return A new {@link ReferencedOptional} with the value or {@code null} if no value in the list equals to the filter
     * @see #filter(Collection, Predicate)
     */
    @NotNull
    public static <T> ReferencedOptional<T> filterToReference(@NotNull Collection<T> in, @NotNull Predicate<T> predicate) {
        return ReferencedOptional.build(filter(in, predicate));
    }

    /**
     * Filters a specific value out of the given map
     *
     * @param in        The given map
     * @param predicate The predicate which checks if the current object equals the the filter
     * @param <K>       The object parameter of the keys in the map
     * @param <V>       The object parameter of the values in the map
     * @return A new {@link ReferencedOptional} with the value or {@code null} if no value in the map equals to the filter
     */
    @NotNull
    public static <K, V> ReferencedOptional<V> filterToReference(@NotNull Map<K, V> in, @NotNull Predicate<K> predicate) {
        if (in.isEmpty()) {
            return ReferencedOptional.empty();
        }

        for (Map.Entry<K, V> entry : in.entrySet()) {
            if (predicate.test(entry.getKey())) {
                return ReferencedOptional.build(entry.getValue());
            }
        }

        return ReferencedOptional.empty();
    }

    /**
     * Filters a value out of the given list and applies the function if the value is non-{@code null}
     *
     * @param in        The given list
     * @param predicate The predicate which checks if the current object equals the the filter
     * @param function  The function which should get applied to the first value which equals to the filter
     * @param <T>       The object parameter of the values in the list
     * @param <F>       The object parameter of the outgoing value
     * @return The value which got applied to the function or {@code null} if no value in the list equals to the filter
     * @see #filter(Collection, Predicate)
     */
    @Nullable
    public static <T, F> F filterAndApply(@NotNull Collection<T> in, @NotNull Predicate<T> predicate, @NotNull Function<T, F> function) {
        if (in.isEmpty()) {
            return null;
        }

        T value = filter(in, predicate);
        return value == null ? null : function.apply(value);
    }

    /**
     * Gets all values in a map which equals to the given filter
     *
     * @param in        The given map
     * @param predicate The predicate which checks if the current object equals the the filter
     * @param <F>       The object parameter of the keys in the map
     * @param <T>       The object parameter of the values in the map
     * @return A list with all values of the map which equaled to the filter
     */
    @NotNull
    public static <F, T> List<T> getValues(@NotNull Map<F, T> in, @NotNull Predicate<F> predicate) {
        if (in.isEmpty()) {
            return new ArrayList<>();
        }

        List<T> out = new ArrayList<>();
        for (Map.Entry<F, T> ftEntry : in.entrySet()) {
            if (predicate.test(ftEntry.getKey())) {
                out.add(ftEntry.getValue());
            }
        }

        return out;
    }

    /**
     * Goes trough all values in a map and applies them to a consumer
     *
     * @param map      The given map
     * @param consumer The consumer which should accept all values in the map
     * @param <T>      The object parameter of the values in the map
     */
    public static <T> void forEachValues(@NotNull Map<?, T> map, @NotNull Consumer<T> consumer) {
        for (T value : map.values()) {
            consumer.accept(value);
        }
    }

    /**
     * Goes through all values in á list and applies them to a consumer
     *
     * @param list     The given list
     * @param consumer The consumer which should accept all values in the map
     * @param <F>      The object parameter of the values in the list
     */
    public static <F> void forEach(@NotNull List<F> list, @NotNull Consumer<F> consumer) {
        for (F f : list) {
            consumer.accept(f);
        }
    }

    /**
     * Applies a function to all keys in the given map
     *
     * @param map        The given map
     * @param fxFunction The function which should get applied to all keys in the map
     * @param <F>        The object parameter of the keys in the map
     * @param <X>        The object parameter of the values in the outgoing list
     * @return The created list with all applied values in the map
     */
    @NotNull
    public static <F, X> List<X> keyApply(@NotNull Map<F, ?> map, @NotNull Function<F, X> fxFunction) {
        List<X> out = new ArrayList<>();
        for (F f : map.keySet()) {
            out.add(fxFunction.apply(f));
        }

        return out;
    }

    /**
     * Creates a new collection
     *
     * @param function The function which creates the collection
     * @param in       The array of the incoming objects
     * @param <S>      The object parameter of the array
     * @param <F>      The object parameter of the values in the outgoing list
     * @return The created list with all values applied to the function
     */
    @NotNull
    @SafeVarargs
    public static <S, F> Collection<F> newCollection(@NotNull Function<S, F> function, S... in) {
        return newCollection(Arrays.asList(in), function);
    }

    /**
     * Applies all values of the incoming collection to the function and collect the values
     *
     * @param in       The incoming collection
     * @param function The function to which the values get applied to
     * @param <S>      The object parameter of the array
     * @param <F>      The object parameter of the values in the outgoing list
     * @return The created collection with the values applied to the function
     */
    @NotNull
    public static <S, F> Collection<F> newCollection(@NotNull Collection<S> in, @NotNull Function<S, F> function) {
        return newCollection(in, s -> true, function);
    }

    /**
     * Applies all values of the incoming collection to the function and collect the values
     *
     * @param in        The incoming collection
     * @param predicate The {@link Predicate} which checks if the value of the list should get into the new one
     * @param function  The function which maps the value of the incoming collection to the outgoing one
     * @param <S>       The object parameter of the collection
     * @param <F>       The object parameter of the values in the outgoing list
     * @return The created collection
     */
    @NotNull
    public static <S, F> Collection<F> newCollection(@NotNull Collection<S> in, @NotNull Predicate<S> predicate, @NotNull Function<S, F> function) {
        Collection<F> out = new ArrayList<>();
        for (S s : in) {
            if (predicate.test(s)) {
                F result = function.apply(s);
                if (result != null) {
                    out.add(result);
                }
            }
        }

        return out;
    }

    /**
     * Filters all values out of the incoming collection which matches to the {@link Predicate}
     *
     * @param list      The incoming list
     * @param predicate The predicate which tests the values in the list
     * @param <T>       The object parameter of the collection
     * @return The new list with all values matching the {@link Predicate}
     */
    @NotNull
    public static <T> List<T> list(@NotNull Collection<T> list, @NotNull Predicate<T> predicate) {
        List<T> out = new ArrayList<>();
        for (T t : list) {
            if (predicate.test(t)) {
                out.add(t);
            }
        }

        return out;
    }

    /**
     * Filters all values out of the incoming collection which not matches to the {@link Predicate}
     *
     * @param list      The incoming list
     * @param predicate The predicate which tests the values in the list
     * @param <T>       The object parameter of the collection
     * @return The new list with all values not matching the {@link Predicate}
     */
    @NotNull
    public static <T> Collection<T> others(@NotNull Collection<T> list, @NotNull Predicate<T> predicate) {
        predicate = predicate.negate();
        Collection<T> out = new ArrayList<>();
        for (T t : list) {
            if (predicate.test(t)) {
                out.add(t);
            }
        }

        return out;
    }

    /**
     * Filters all values out of the incoming collection which matches to the {@link Predicate}
     *
     * @param collection The incoming collection
     * @param predicate  The predicate which tests the values in the list
     * @param <T>        The object parameter of the collection
     * @return The new list with all values matching the {@link Predicate}
     */
    @NotNull
    public static <T> Collection<T> allOf(@NotNull Collection<T> collection, @NotNull Predicate<T> predicate) {
        Collection<T> out = new ArrayList<>();
        for (T t : collection) {
            if (predicate.test(t)) {
                out.add(t);
            }
        }

        return out;
    }

    /**
     * Applies a function to the given list
     *
     * @param collection The collection on which the function should get applied
     * @param function   The function which should get applied
     * @param <T>        The object parameter of the collection
     * @param <F>        The object parameter of the values in the outgoing collection
     * @return The new collection with all values applied to the function in it
     */
    @NotNull
    public static <T, F> Collection<F> apply(@NotNull Collection<T> collection, @NotNull Function<T, F> function) {
        Collection<F> out = new ArrayList<>();
        for (T t : collection) {
            out.add(function.apply(t));
        }

        return out;
    }

    @NotNull
    public static <T> Collection<T> fromIterator(@NotNull Iterator<T> iterator) {
        Collection<T> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }

        return result;
    }

    /**
     * Contacts two arrays
     *
     * @param first  The base array which should get concatenated with the other
     * @param second The array which should be added to the base array
     * @param <T>    The type of the arrays which should get concatenated
     * @return The concatenated array of the first and second array
     */
    @NotNull
    public static <T> T[] concat(@NotNull T[] first, @NotNull T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static <T> int count(@NotNull Collection<T> collection, @NotNull Predicate<T> predicate) {
        AtomicInteger atomicInteger = new AtomicInteger();
        for (T t : collection) {
            if (predicate.test(t)) {
                atomicInteger.getAndIncrement();
            }
        }

        return atomicInteger.get();
    }

    @NotNull
    public static <I, O> Collection<O> map(@NotNull Collection<I> collection, @NotNull Function<I, O> mapper) {
        Collection<O> result = new ArrayList<>();
        for (I i : collection) {
            O out = mapper.apply(i);
            if (out != null) {
                result.add(out);
            }
        }

        return result;
    }

    @NotNull
    public static <I, O> Collection<O> map(@NotNull I[] collection, @NotNull Function<I, O> mapper) {
        Collection<O> result = new ArrayList<>();
        for (I i : collection) {
            O out = mapper.apply(i);
            if (out != null) {
                result.add(out);
            }
        }

        return result;
    }

    public static <T> boolean hasMatch(@NotNull Collection<T> collection, @NotNull Predicate<T> predicate) {
        for (T t : collection) {
            if (predicate.test(t)) {
                return true;
            }
        }

        return false;
    }
}
