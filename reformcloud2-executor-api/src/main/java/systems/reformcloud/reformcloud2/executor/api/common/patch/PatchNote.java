package systems.reformcloud.reformcloud2.executor.api.common.patch;

import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

public interface PatchNote extends Nameable {

    /**
     * @return The new version string after the patch
     */
    String newVersion();

    /**
     * @return The update message of the patch
     */
    String updateMessage();
}
