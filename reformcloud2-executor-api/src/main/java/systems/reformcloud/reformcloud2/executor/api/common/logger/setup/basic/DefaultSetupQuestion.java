package systems.reformcloud.reformcloud2.executor.api.common.logger.setup.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.logger.setup.SetupQuestion;

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

    @NotNull
    @Override
    public String question() {
        return question;
    }

    @NotNull
    @Override
    public String wrongAnswerMessage() {
        return wrongAnswer;
    }

    @NotNull
    @Override
    public Predicate<String> tester() {
        return predicate;
    }

    @NotNull
    @Override
    public Consumer<String> then() {
        return then;
    }
}
