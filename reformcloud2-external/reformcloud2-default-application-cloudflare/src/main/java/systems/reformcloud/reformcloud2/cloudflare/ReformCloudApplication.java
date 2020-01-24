package systems.reformcloud.reformcloud2.cloudflare;

import systems.reformcloud.reformcloud2.cloudflare.api.CloudFlareHelper;
import systems.reformcloud.reformcloud2.cloudflare.listener.ProcessListener;
import systems.reformcloud.reformcloud2.cloudflare.update.CloudFlareAddonUpdater;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.api.Application;
import systems.reformcloud.reformcloud2.executor.api.common.application.language.ApplicationLanguage;
import systems.reformcloud.reformcloud2.executor.api.common.application.updater.ApplicationUpdateRepository;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ReformCloudApplication extends Application {

    private static final ProcessListener LISTENER = new ProcessListener();

    private static final ApplicationUpdateRepository REPOSITORY = new CloudFlareAddonUpdater();

    private static boolean loaded = false;

    @Override
    public void onEnable() {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("language-cloudflare.properties")) {
            Properties properties = new Properties();
            properties.load(stream);

            LanguageManager.loadAddonMessageFile(getApplication().getName(), new ApplicationLanguage(
                    getApplication().getName(), properties
            ));
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        if (CloudFlareHelper.init(dataFolder().getPath())) {
            System.err.println(LanguageManager.get("cloudflare-first-init"));
            return;
        }

        ExecutorAPI.getInstance().getEventManager().registerListener(LISTENER);
        CloudFlareHelper.loadAlreadyRunning();
        loaded = true;
    }

    @Override
    public void onPreDisable() {
        if (!loaded) {
            return;
        }

        CloudFlareHelper.handleStop();
        ExecutorAPI.getInstance().getEventManager().unregisterListener(LISTENER);
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getAllProcesses().forEach(CloudFlareHelper::deleteRecord);
    }

    @Nullable
    @Override
    public ApplicationUpdateRepository getUpdateRepository() {
        return REPOSITORY;
    }
}
