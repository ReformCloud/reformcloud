package systems.reformcloud.reformcloud2.executor.api.common.logger.setup.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.logger.LoggerBase;
import systems.reformcloud.reformcloud2.executor.api.common.logger.setup.Setup;
import systems.reformcloud.reformcloud2.executor.api.common.logger.setup.SetupQuestion;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class DefaultSetup implements Setup {

    private final Queue<SetupQuestion> questions = new ConcurrentLinkedQueue<>();

    @NotNull
    @Override
    public Setup addQuestion(@NotNull SetupQuestion setupQuestion) {
        questions.add(setupQuestion);
        return this;
    }

    @Override
    public void startSetup(@NotNull LoggerBase loggerBase) {
        questions.forEach(setupQuestion -> {
            System.out.println(setupQuestion.question());
            String line = loggerBase.readLineNoPrompt();
            while (line.trim().isEmpty() || !setupQuestion.tester().test(line)) {
                System.out.println(setupQuestion.wrongAnswerMessage());
                line = loggerBase.readLineNoPrompt();
            }

            setupQuestion.then().accept(line);
        });
    }

    @Override
    public void clear() {
        this.questions.clear();
    }
}
