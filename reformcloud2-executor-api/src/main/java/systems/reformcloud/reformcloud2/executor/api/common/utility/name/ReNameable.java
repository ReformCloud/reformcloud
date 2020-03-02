package systems.reformcloud.reformcloud2.executor.api.common.utility.name;

import javax.annotation.Nonnull;

/**
 * A special nameable which can get renamed
 */
public interface ReNameable extends Nameable {

    /**
     * Sets a new name for the current nameable object
     *
     * @param newName The new name which should get used
     */
    void setName(@Nonnull String newName);
}
