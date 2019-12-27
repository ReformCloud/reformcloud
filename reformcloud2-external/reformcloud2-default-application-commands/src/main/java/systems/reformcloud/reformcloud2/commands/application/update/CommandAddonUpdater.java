package systems.reformcloud.reformcloud2.commands.application.update;

import systems.reformcloud.reformcloud2.commands.application.ReformCloudApplication;
import systems.reformcloud.reformcloud2.executor.api.common.application.updater.ApplicationRemoteUpdate;
import systems.reformcloud.reformcloud2.executor.api.common.application.updater.basic.BasicApplicationRemoteUpdate;
import systems.reformcloud.reformcloud2.executor.api.common.application.updater.basic.DefaultApplicationUpdateRepository;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.DownloadHelper;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Properties;

public class CommandAddonUpdater extends DefaultApplicationUpdateRepository {

    private String newVersion;

    @Override
    public void fetchOrigin() {
        DownloadHelper.openConnection("https://internal.reformcloud.systems/version.properties", inputStream -> {
            try {
                Properties properties = new Properties();
                properties.load(inputStream);

                newVersion = properties.getProperty("version");
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public boolean isNewVersionAvailable() {
        return !ReformCloudApplication.self().getApplication().applicationConfig().version().equals(newVersion);
    }

    @Nullable
    @Override
    public ApplicationRemoteUpdate getUpdate() {
        if (!isNewVersionAvailable()) {
            return null;
        }

        return new BasicApplicationRemoteUpdate(newVersion,
                "https://dl.reformcloud.systems/addonsv2/reformcloud2-default-application-commands-" + newVersion + ".jar");
    }
}
