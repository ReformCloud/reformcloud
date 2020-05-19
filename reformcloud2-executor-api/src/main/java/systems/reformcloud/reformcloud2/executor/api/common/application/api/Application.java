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
package systems.reformcloud.reformcloud2.executor.api.common.application.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.application.LoadedApplication;
import systems.reformcloud.reformcloud2.executor.api.common.application.factory.ApplicationThreadFactory;
import systems.reformcloud.reformcloud2.executor.api.common.application.factory.ApplicationThreadGroup;
import systems.reformcloud.reformcloud2.executor.api.common.application.language.ApplicationLanguage;
import systems.reformcloud.reformcloud2.executor.api.common.application.loader.AppClassLoader;
import systems.reformcloud.reformcloud2.executor.api.common.application.updater.ApplicationUpdateRepository;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.language.language.Language;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Application {

    private static Application self;
    private LoadedApplication application;
    private ExecutorService executorService;
    private AppClassLoader appClassLoader;

    public static Application self() {
        return self;
    }

    public final void init(@NotNull LoadedApplication application, AppClassLoader loader) {
        self = this;

        this.application = application;
        this.executorService = Executors.newCachedThreadPool(new ApplicationThreadFactory(new ApplicationThreadGroup(application)));
        this.appClassLoader = loader;
    }

    public void onInstallable() {
    }

    public void onInstalled() {
    }

    public void onLoad() {
    }

    public void onEnable() {
    }

    public void onPreDisable() {
    }

    public void onDisable() {
    }

    public void onUninstall() {
    }

    @Nullable
    public ApplicationUpdateRepository getUpdateRepository() {
        return null;
    }

    @NotNull
    public final File dataFolder() {
        return new File("reformcloud/applications", this.application.getName());
    }

    @Nullable
    public final InputStream getResourceAsStream(String name) {
        return this.getClass().getClassLoader().getResourceAsStream(name);
    }

    public final void registerLanguage(Properties properties) {
        Language language = new ApplicationLanguage(this.application.getName(), properties);
        LanguageManager.loadAddonMessageFile(this.application.getName(), language);
    }

    public final void unloadAllLanguageFiles() {
        LanguageManager.unregisterMessageFile(this.application.getName());
    }

    public final AppClassLoader getAppClassLoader() {
        return this.appClassLoader;
    }

    @NotNull
    public final LoadedApplication getApplication() {
        return this.application;
    }

    public void log(String log) {
        System.out.println(log);
    }

    @NotNull
    public final ExecutorService getExecutorService() {
        return this.executorService;
    }
}
