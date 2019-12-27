package systems.reformcloud.reformcloud2.executor.api.common.application.updater.basic;

import systems.reformcloud.reformcloud2.executor.api.common.application.updater.ApplicationRemoteUpdate;

public class BasicApplicationRemoteUpdate implements ApplicationRemoteUpdate {

    private String version;

    private String downloadURL;

    public BasicApplicationRemoteUpdate(String version, String downloadURL) {
        this.version = version;
        this.downloadURL = downloadURL;
    }

    @Override
    public String getNewVersion() {
        return this.version;
    }

    @Override
    public String getDownloadUrl() {
        return this.downloadURL;
    }
}
