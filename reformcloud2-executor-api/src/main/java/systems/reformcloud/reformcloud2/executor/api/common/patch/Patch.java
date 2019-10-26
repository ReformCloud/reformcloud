package systems.reformcloud.reformcloud2.executor.api.common.patch;

public interface Patch {

    /**
     * @return The release date of the patch
     */
    long getReleaseDate();

    /**
     * @return The download url of the patch
     */
    String getDownloadURL();
}
