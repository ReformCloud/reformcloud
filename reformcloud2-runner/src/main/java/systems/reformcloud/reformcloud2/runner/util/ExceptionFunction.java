package systems.reformcloud.reformcloud2.runner.util;

@FunctionalInterface
public interface ExceptionFunction<T, R> {

    R apply(T t) throws Exception;
}
