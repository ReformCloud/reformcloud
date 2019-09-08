package de.klaro.reformcloud2.executor.api.common.utility.list;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Links {

    public static List<String> toLowerCase(List<String> list) {
        List<String> strings = new ArrayList<>();
        list.forEach(new Consumer<String>() {
            @Override
            public void accept(String string) {
                strings.add(string.toLowerCase());
            }
        });

        return strings;
    }

    public static <T> List<T> unmodifiable(List<T> in) {
        return Collections.unmodifiableList(in);
    }

    public static <T> List<T> newList(Collection<T> in) {
        return new ArrayList<>(in);
    }

    public static <T, F> List<F> apply(List<T> in, Function<T, F> function) {
        List<F> out = new ArrayList<>();
        in.forEach(new Consumer<T>() {
            @Override
            public void accept(T t) {
                out.add(function.apply(t));
            }
        });

        return out;
    }

    public static <T> T filter(Collection<T> in, Predicate<T> predicate) {
        for (T t : in) {
            if (predicate.test(t)) {
                return t;
            }
        }

        return null;
    }

    public static <T> Optional<T> filterToOptional(Collection<T> in, Predicate<T> predicate) {
        for (T t : in) {
            if (predicate.test(t)) {
                return Optional.of(t);
            }
        }

        return Optional.empty();
    }

    public static <T, F> F filterAndApply(List<T> in, Predicate<T> predicate, Function<T, F> function) {
        for (T t : in) {
            if (predicate.test(t)) {
                return function.apply(t);
            }
        }

        return null;
    }

    public static <F, T> List<T> getValues(Map<F, T> in, Predicate<F> predicate) {
        List<T> out = new ArrayList<>();
        in.entrySet().forEach(new Consumer<Map.Entry<F, T>>() {
            @Override
            public void accept(Map.Entry<F, T> ftEntry) {
                if (predicate.test(ftEntry.getKey())) {
                    out.add(ftEntry.getValue());
                }
            }
        });
        return out;
    }

    public static <F, T> void forEachValues(Map<F, T> map, Consumer<T> consumer) {
        map.forEach(new BiConsumer<F, T>() {
            @Override
            public void accept(F f, T t) {
                consumer.accept(t);
            }
        });
    }

    public static <F> void forEach(List<F> list, Consumer<F> consumer) {
        list.forEach(consumer);
    }

    public static <F, T, X> List<X> keyApply(Map<F, T> map, Function<F, X> fxFunction) {
        List<X> out = new ArrayList<>();
        map.keySet().forEach(new Consumer<F>() {
            @Override
            public void accept(F f) {
                out.add(fxFunction.apply(f));
            }
        });

        return out;
    }

    @SafeVarargs
    public static <S, F> Collection<F> newCollection(Function<S, F> function, S... in) {
        return newCollection(Arrays.asList(in), function);
    }

    public static <S, F> Collection<F> newCollection(List<S> in, Function<S, F> function) {
        return newCollection(in, new Predicate<S>() {
            @Override
            public boolean test(S s) {
                return true;
            }
        }, function);
    }

    public static <S, F> Collection<F> newCollection(List<S> in, Predicate<S> predicate, Function<S, F> function) {
        Collection<F> out = new LinkedList<>();
        in.forEach(new Consumer<S>() {
            @Override
            public void accept(S s) {
                if (predicate.test(s)) {
                    out.add(function.apply(s));
                }
            }
        });

        return out;
    }

    public static <T> List<T> list(Collection<T> list, Predicate<T> predicate) {
        List<T> out = new LinkedList<>();
        list.forEach(new Consumer<T>() {
            @Override
            public void accept(T t) {
                if (predicate.test(t)) {
                    out.add(t);
                }
            }
        });
        return out;
    }

    public static <T> Collection<T> allOf(Collection<T> collection, Predicate<T> predicate) {
        Collection<T> out = new LinkedList<>();
        collection.forEach(new Consumer<T>() {
            @Override
            public void accept(T t) {
                if (predicate.test(t)) {
                    out.add(t);
                }
            }
        });
        return out;
    }

    public static <F, T> Collection<T> arrayCollect(F[] fs, Function<F, T> ftFunction) {
        Collection<T> collection = new LinkedList<>();
        for (F f : fs) {
            collection.add(ftFunction.apply(f));
        }

        return collection;
    }

    public static <T, F> Collection<F> apply(Collection<T> collection, Function<T, F> function) {
        Collection<F> out = new LinkedList<>();
        collection.forEach(new Consumer<T>() {
            @Override
            public void accept(T t) {
                out.add(function.apply(t));
            }
        });
        return out;
    }

    public static <E> Optional<E> toOptional(E in) {
        return Optional.ofNullable(in);
    }
}
