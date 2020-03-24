package systems.reformcloud.reformcloud2.runner.updater;

import javax.annotation.Nonnull;

/**
 * Represents an updater which can update the file or the system
 */
public interface Updater {

    /**
     * Collects the needed information for the updater to run
     */
    void collectInformation();

    /**
     * @return If the target of the updater has updates available
     */
    boolean hasNewVersion();

    /**
     * Applies the updates which are available
     */
    void applyUpdates();

    /**
     * @return The name of the current updater
     */
    @Nonnull
    String getName();

}
