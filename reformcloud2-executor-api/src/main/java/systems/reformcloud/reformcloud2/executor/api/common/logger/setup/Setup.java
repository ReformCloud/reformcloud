package systems.reformcloud.reformcloud2.executor.api.common.logger.setup;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.logger.LoggerBase;

public interface Setup {

    /**
     * Adds a question to the current setup
     *
     * @param setupQuestion The question which should be used
     * @return The current instance of the setup
     */
    @NotNull
    Setup addQuestion(@NotNull SetupQuestion setupQuestion);

    /**
     * Starts the current setup
     *
     * @param loggerBase The {@link LoggerBase} which should get used for the setup
     */
    void startSetup(@NotNull LoggerBase loggerBase);

    /**
     * Removes all previously added questions from the current setup
     */
    void clear();
}
