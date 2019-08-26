package de.klaro.reformcloud2.executor.api.common.logger.setup.basic;

import de.klaro.reformcloud2.executor.api.common.logger.setup.SetupQuestion;

import java.util.function.Consumer;
import java.util.function.Predicate;

public final class DefaultSetupQuestion implements SetupQuestion {

    public DefaultSetupQuestion(String question, String wrongAnswer, Predicate<String> predicate, Consumer<String> then) {
        this.question = question;
        this.wrongAnswer = wrongAnswer;
        this.predicate = predicate;
        this.then = then;
    }

    private final String question;

    private final String wrongAnswer;

    private final Predicate<String> predicate;

    private final Consumer<String> then;

    @Override
    public String question() {
        return question;
    }

    @Override
    public String wrongAnswerMessage() {
        return wrongAnswer;
    }

    @Override
    public Predicate<String> tester() {
        return predicate;
    }

    @Override
    public Consumer<String> then() {
        return then;
    }
}
