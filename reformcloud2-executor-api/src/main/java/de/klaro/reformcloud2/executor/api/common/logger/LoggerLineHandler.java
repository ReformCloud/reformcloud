package de.klaro.reformcloud2.executor.api.common.logger;

public interface LoggerLineHandler {

    default void handleLine(String line, LoggerBase base) {

    }

    default void handleRaw(String line, LoggerBase base) {

    }
}
