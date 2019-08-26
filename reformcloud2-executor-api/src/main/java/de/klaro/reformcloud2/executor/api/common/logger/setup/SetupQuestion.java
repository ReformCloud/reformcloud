package de.klaro.reformcloud2.executor.api.common.logger.setup;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface SetupQuestion {

    String question();

    String wrongAnswerMessage();

    Predicate<String> tester();

    Consumer<String> then();
}
