package systems.reformcloud.reformcloud2.executor.api.common.logger.setup;

import systems.reformcloud.reformcloud2.executor.api.common.logger.LoggerBase;

public interface Setup {

    Setup addQuestion(SetupQuestion setupQuestion);

    void startSetup(LoggerBase loggerBase);
}
