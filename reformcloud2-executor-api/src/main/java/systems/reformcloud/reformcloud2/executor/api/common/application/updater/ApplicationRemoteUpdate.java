package systems.reformcloud.reformcloud2.executor.api.common.application.updater;

public interface ApplicationRemoteUpdate {

    /**
     * @return The new version string of the app after the update
     */
    String getNewVersion();

    /**
     * @return The url to download the update
     */
    String getDownloadUrl();
}
