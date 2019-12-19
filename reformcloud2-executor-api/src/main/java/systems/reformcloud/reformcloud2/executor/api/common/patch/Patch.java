package systems.reformcloud.reformcloud2.executor.api.common.patch;

import javax.annotation.Nonnull;

public interface Patch {

    /**
     * @return The release date of the patch
     */
    long getReleaseDate();

    /**
     * @return The download url of the patch
     */
    @Nonnull
    String getDownloadURL();
}
