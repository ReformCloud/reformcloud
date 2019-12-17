package systems.reformcloud.reformcloud2.executor.api.common.logger.setup;

import systems.reformcloud.reformcloud2.executor.api.common.logger.LoggerBase;

import javax.annotation.Nonnull;

public interface Setup {

    /**
     * Adds a question to the current setup
     *
     * @param setupQuestion The question which should be used
     * @return The current instance of the setup
     */
    @Nonnull
    Setup addQuestion(@Nonnull SetupQuestion setupQuestion);

    /**
     * Starts the current setup
     *
     * @param loggerBase The {@link LoggerBase} which should get used for the setup
     */
    void startSetup(@Nonnull LoggerBase loggerBase);
}
