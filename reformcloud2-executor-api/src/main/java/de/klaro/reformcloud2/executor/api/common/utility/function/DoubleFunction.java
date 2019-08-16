package de.klaro.reformcloud2.executor.api.common.utility.function;

@FunctionalInterface
public interface DoubleFunction<S, V, F> {

    Double<V, F> apply(S s);
}
