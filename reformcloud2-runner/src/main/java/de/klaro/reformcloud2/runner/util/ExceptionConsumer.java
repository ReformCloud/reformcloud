package de.klaro.reformcloud2.runner.util;

@FunctionalInterface
public interface ExceptionConsumer<T> {

    void accept(T t) throws Exception;
}
