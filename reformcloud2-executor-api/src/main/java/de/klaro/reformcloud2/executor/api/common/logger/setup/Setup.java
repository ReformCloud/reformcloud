package de.klaro.reformcloud2.executor.api.common.logger.setup;

import de.klaro.reformcloud2.executor.api.common.logger.LoggerBase;

public interface Setup {

    Setup addQuestion(SetupQuestion setupQuestion);

    void startSetup(LoggerBase loggerBase);
}
