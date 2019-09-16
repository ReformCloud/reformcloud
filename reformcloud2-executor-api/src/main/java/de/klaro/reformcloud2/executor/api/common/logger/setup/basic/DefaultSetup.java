package de.klaro.reformcloud2.executor.api.common.logger.setup.basic;

import de.klaro.reformcloud2.executor.api.common.logger.LoggerBase;
import de.klaro.reformcloud2.executor.api.common.logger.setup.Setup;
import de.klaro.reformcloud2.executor.api.common.logger.setup.SetupQuestion;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class DefaultSetup implements Setup {

    private final Queue<SetupQuestion> questions = new ConcurrentLinkedQueue<>();

    @Override
    public Setup addQuestion(SetupQuestion setupQuestion) {
        questions.add(setupQuestion);
        return this;
    }

    @Override
    public void startSetup(LoggerBase loggerBase) {
        questions.forEach(setupQuestion -> {
            System.out.println(setupQuestion.question());
            String line = loggerBase.readLineNoPrompt();
            while (line.trim().isEmpty() || !setupQuestion.tester().test(line)) {
                System.out.println(setupQuestion.wrongAnswerMessage());
                line = loggerBase.readLine();
            }

            setupQuestion.then().accept(line);
        });
    }
}
