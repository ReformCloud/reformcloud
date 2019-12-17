package systems.reformcloud.reformcloud2.executor.api.common.logger.setup.basic;

import systems.reformcloud.reformcloud2.executor.api.common.logger.LoggerBase;
import systems.reformcloud.reformcloud2.executor.api.common.logger.setup.Setup;
import systems.reformcloud.reformcloud2.executor.api.common.logger.setup.SetupQuestion;

import javax.annotation.Nonnull;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class DefaultSetup implements Setup {

    private final Queue<SetupQuestion> questions = new ConcurrentLinkedQueue<>();

    @Nonnull
    @Override
    public Setup addQuestion(@Nonnull SetupQuestion setupQuestion) {
        questions.add(setupQuestion);
        return this;
    }

    @Override
    public void startSetup(@Nonnull LoggerBase loggerBase) {
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
}
