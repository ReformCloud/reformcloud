/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.cloudflare;

import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.cloudflare.api.CloudFlareHelper;
import systems.reformcloud.reformcloud2.cloudflare.listener.ProcessListener;
import systems.reformcloud.reformcloud2.cloudflare.update.CloudFlareAddonUpdater;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.api.Application;
import systems.reformcloud.reformcloud2.executor.api.common.application.language.ApplicationLanguage;
import systems.reformcloud.reformcloud2.executor.api.common.application.updater.ApplicationUpdateRepository;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;

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
