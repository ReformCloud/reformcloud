package systems.reformcloud.reformcloud2.executor.api.common.patch;

import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import javax.annotation.Nonnull;

public interface PatchNote extends Nameable {

    /**
     * @return The new version string after the patch
     */
    @Nonnull
    String newVersion();

    /**
     * @return The update message of the patch
     */
    @Nonnull
    String updateMessage();
}
