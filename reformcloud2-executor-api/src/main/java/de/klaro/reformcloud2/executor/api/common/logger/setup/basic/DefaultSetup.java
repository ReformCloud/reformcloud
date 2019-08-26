package de.klaro.reformcloud2.executor.api.common.logger.setup.basic;

import de.klaro.reformcloud2.executor.api.common.logger.LoggerBase;
import de.klaro.reformcloud2.executor.api.common.logger.setup.Setup;
import de.klaro.reformcloud2.executor.api.common.logger.setup.SetupQuestion;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;

public final class DefaultSetup implements Setup {

    private final SortedSet<SetupQuestion> questions = new TreeSet<>();

    @Override
    public Setup addQuestion(SetupQuestion setupQuestion) {
        questions.add(setupQuestion);
        return this;
    }

    @Override
    public void startSetup(LoggerBase loggerBase) {
        questions.forEach(new Consumer<SetupQuestion>() {
            @Override
            public void accept(SetupQuestion setupQuestion) {
                System.out.println(setupQuestion.question());
                String line = loggerBase.readLine();
                while (line.trim().isEmpty() || !setupQuestion.tester().test(line)) {
                    System.out.println(setupQuestion.wrongAnswerMessage());
                    line = loggerBase.readLine();
                }

                setupQuestion.then().accept(line);
            }
        });
    }
}
