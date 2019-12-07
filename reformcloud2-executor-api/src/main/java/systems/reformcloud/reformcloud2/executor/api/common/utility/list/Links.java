package systems.reformcloud.reformcloud2.executor.api.common.utility.list;

import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class Links {

    private Links() {
        throw new UnsupportedOperationException();
    }

    /**
     * Makes all strings in the list lower-case
     *
     * @param list The input list
     * @return A new list with all keys of the given list, but lower-case
     */
    @Nonnull
    public static List<String> toLowerCase(@Nonnull Collection<String> list) {
        List<String> strings = new ArrayList<>();
        list.forEach(string -> strings.add(string.toLowerCase()));

        return strings;
    }

    /**
     * Makes the given list unmodifiable
     *
     * @param in The list which should be unmodifiable
     * @param <T> The object parameter of the values in the list
     * @return A unmodifiable list of all given keys
     */
    @Nonnull
    public static <T> List<T> unmodifiable(@Nonnull List<T> in) {
        return Collections.unmodifiableList(in);
    }

    /**
     * Creates a new list with all given parameters
     *
     * @param in The given keys which should be in the new list
     * @param <T> The object parameter of the values in the list
     * @return A new list with all values of the input list in it
     */
    @Nonnull
    public static <T> List<T> newList(@Nonnull Collection<T> in) {
        return new ArrayList<>(in);
    }

    /**
     * Copies a sorted set including the {@link Comparator} and all values in it
     *
     * @param set The incoming set with all values in it
     * @param <T> The object parameter of the values in the list
     * @return A copy of the current set
     */
    @Nonnull
    public static <T> SortedSet<T> copySortedSet(@Nonnull SortedSet<T> set) {
        SortedSet<T> sortedSet = new TreeSet<>(set.comparator());
        sortedSet.addAll(set);
        return sortedSet;
    }

    /**
     * Applies a function to all values in the given list
     *
     * @param in The incoming list with the key values in it
     * @param function The function which should get applied to the values
     * @param <T> The object parameter of the values in the list
     * @param <F> The object parameter of the values in the outgoing list
     * @return A new list with all values of the incoming list, applied to the function
     */
    @Nonnull
    public static <T, F> List<F> apply(@Nonnull List<T> in, @Nonnull Function<T, F> function) {
        return in.stream().map(function).collect(Collectors.toList());
    }

    /**
     * Filters a specific value out of the given list
     *
     * @param in The incoming list
     * @param predicate The predicate which checks if the current object equals the the filter
     * @param <T> The object parameter of the values in the list
     * @return The first value in the list which equals to the filter
     */
    @Nullable
    public static <T> T filter(@Nonnull Collection<T> in, @Nonnull Predicate<T> predicate) {
        if (in.isEmpty()) {
            return null;
        }

        return in.stream().filter(predicate).findFirst().orElse(null);
    }

    /**
     * Filters a specific value out of the given list
     *
     * @see #filter(Collection, Predicate)
     * @param in The incoming list
     * @param predicate The predicate which checks if the current object equals the the filter
     * @param <T> The object parameter of the values in the list
     * @return A new {@link ReferencedOptional} with the value or {@code null} if no value in the list equals to the filter
     */
    @Nonnull
    public static <T> ReferencedOptional<T> filterToReference(@Nonnull Collection<T> in, @Nonnull Predicate<T> predicate) {
        return ReferencedOptional.build(filter(in, predicate));
    }

    /**
     * Filters a specific value out of the given map
     *
     * @param in The given map
     * @param predicate The predicate which checks if the current object equals the the filter
     * @param <K> The object parameter of the keys in the map
     * @param <V> The object parameter of the values in the map
     * @return A new {@link ReferencedOptional} with the value or {@code null} if no value in the map equals to the filter
     */
    @Nonnull
    public static <K, V> ReferencedOptional<V> filterToReference(@Nonnull Map<K, V> in, @Nonnull Predicate<K> predicate) {
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
     * @see #filter(Collection, Predicate)
     * @param in The given list
     * @param predicate The predicate which checks if the current object equals the the filter
     * @param function The function which should get applied to the first value which equals to the filter
     * @param <T> The object parameter of the values in the list
     * @param <F> The object parameter of the outgoing value
     * @return The value which got applied to the function or {@code null} if no value in the list equals to the filter
     */
    @Nullable
    public static <T, F> F filterAndApply(@Nonnull List<T> in, @Nonnull Predicate<T> predicate, @Nonnull Function<T, F> function) {
        if (in.isEmpty()) {
            return null;
        }

        T value = filter(in, predicate);
        return value == null ? null : function.apply(value);
    }

    /**
     * Gets all values in a map which equals to the given filter
     *
     * @param in The given map
     * @param predicate The predicate which checks if the current object equals the the filter
     * @param <F> The object parameter of the keys in the map
     * @param <T> The object parameter of the values in the map
     * @return A list with all values of the map which equaled to the filter
     */
    @Nonnull
    public static <F, T> List<T> getValues(@Nonnull Map<F, T> in, @Nonnull Predicate<F> predicate) {
        if (in.isEmpty()) {
            return new ArrayList<>();
        }

        List<T> out = new ArrayList<>();
        in.forEach((key, value) -> {
            if (predicate.test(key)) {
                out.add(value);
            }
        });
        return out;
    }

    /**
     * Goes trough all values in a map and applies them to a consumer
     *
     * @param map The given map
     * @param consumer The consumer which should accept all values in the map
     * @param <T> The object parameter of the values in the map
     */
    public static <T> void forEachValues(@Nonnull Map<?, T> map, @Nonnull Consumer<T> consumer) {
        map.values().forEach(consumer);
    }

    /**
     * Goes through all values in á list and applies them to a consumer
     *
     * @param list The given list
     * @param consumer The consumer which should accept all values in the map
     * @param <F> The object parameter of the values in the list
     */
    public static <F> void forEach(@Nonnull List<F> list, @Nonnull Consumer<F> consumer) {
        list.forEach(consumer);
    }

    /**
     * Applies a function to all keys in the given map
     *
     * @param map The given map
     * @param fxFunction The function which should get applied to all keys in the map
     * @param <F> The object parameter of the keys in the map
     * @param <X>
     * @return
     */
    public static <F, X> List<X> keyApply(Map<F, ?> map, Function<F, X> fxFunction) {
        List<X> out = new ArrayList<>();
        map.keySet().forEach(f -> out.add(fxFunction.apply(f)));

        return out;
    }

    @SafeVarargs
    public static <S, F> Collection<F> newCollection(Function<S, F> function, S... in) {
        return newCollection(Arrays.asList(in), function);
    }

    public static <S, F> Collection<F> newCollection(Collection<S> in, Function<S, F> function) {
        return newCollection(in, s -> true, function);
    }

    public static <S, F> Collection<F> newCollection(Collection<S> in, Predicate<S> predicate, Function<S, F> function) {
        Collection<F> out = new LinkedList<>();
        in.forEach(s -> {
            if (predicate.test(s)) {
                out.add(function.apply(s));
            }
        });

        return out;
    }

    public static <T> List<T> list(Collection<T> list, Predicate<T> predicate) {
        List<T> out = new LinkedList<>();
        list.forEach(t -> {
            if (predicate.test(t)) {
                out.add(t);
            }
        });
        return out;
    }

    public static <T> Collection<T> others(Collection<T> list, Predicate<T> predicate) {
        List<T> out = new LinkedList<>();
        list.forEach(t -> {
            if (!predicate.test(t)) {
                out.add(t);
            }
        });
        return out;
    }

    public static <T> Collection<T> allOf(Collection<T> collection, Predicate<T> predicate) {
        Collection<T> out = new LinkedList<>();
        collection.forEach(t -> {
            if (predicate.test(t)) {
                out.add(t);
            }
        });
        return out;
    }

    public static <T, F> Collection<F> apply(Collection<T> collection, Function<T, F> function) {
        Collection<F> out = new LinkedList<>();
        collection.forEach(t -> out.add(function.apply(t)));
        return out;
    }

    public static <F, S> Collection<S> deepFilter(Map<F, S> in, Predicate<Map.Entry<F, S>> predicate) {
        Collection<S> out = new LinkedList<>();
        in.entrySet().forEach(e -> {
            if (predicate.test(e)) {
                out.add(e.getValue());
            }
        });
        return out;
    }

    public static <F, S> ReferencedOptional<Map.Entry<F, S>> deepFilterToReference(Map<F, S> in, Predicate<Map.Entry<F, S>> predicate) {
        if (in.isEmpty()) {
            return ReferencedOptional.empty();
        }

        for (Map.Entry<F, S> fsEntry : in.entrySet()) {
            if (predicate.test(fsEntry)) {
                return ReferencedOptional.build(fsEntry);
            }
        }

        return ReferencedOptional.empty();
    }
}
