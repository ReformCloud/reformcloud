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
package systems.reformcloud.reformcloud2.backends.url;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.TemplateBackend;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.TemplateBackendManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.DownloadHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

public final class URLTemplateBackend implements TemplateBackend {

    private final String basePath;

    private URLTemplateBackend(JsonConfiguration configuration) {
        this.basePath = configuration.getString("baseUrl");
    }

    public static void load(String basePath) {
        if (!Files.exists(Paths.get(basePath + "/url.json"))) {
            new JsonConfiguration()
                    .add("baseUrl", "https://127.0.0.1/rc/templates")
                    .write(Paths.get(basePath + "/url.json"));
        }

        TemplateBackendManager.registerBackend(new URLTemplateBackend(JsonConfiguration.read(Paths.get(basePath + "/url.json"))));
    }

    public static void unload() {
        TemplateBackendManager.unregisterBackend("URL");
    }

    @Override
    public boolean existsTemplate(@NotNull String group, @NotNull String template) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(getBasePath() + group + "-" + template + ".zip").openConnection();
            connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11"
            );
            connection.setUseCaches(false);
            connection.connect();

            return true;
        } catch (final IOException ex) {
            return false;
        }
    }

    @Override
    public void createTemplate(@NotNull String group, @NotNull String template) {
    }

    @NotNull
    @Override
    public Task<Void> loadTemplate(@NotNull String group, @NotNull String template, @NotNull Path target) {
        DownloadHelper.downloadAndDisconnect(getBasePath() + group + "-" + template + ".zip", "reformcloud/files/temp/template.zip");
        SystemHelper.unZip(new File("reformcloud/files/temp/template.zip"), target.toString());
        SystemHelper.deleteFile(new File("reformcloud/files/temp/template.zip"));
        return Task.completedTask(null);
    }

    @NotNull
    @Override
    public Task<Void> loadGlobalTemplates(@NotNull ProcessGroup group, @NotNull Path target) {
        Streams.allOf(group.getTemplates(), e -> e.getBackend().equals(getName())
                && e.isGlobal()).forEach(e -> this.loadTemplate(group.getName(), e.getName(), target));
        return Task.completedTask(null);
    }

    @NotNull
    @Override
    public Task<Void> loadPath(@NotNull String path, @NotNull Path target) {
        DownloadHelper.downloadAndDisconnect(getBasePath() + path, "reformcloud/files/temp/template.zip");
        SystemHelper.unZip(new File("reformcloud/files/temp/template.zip"), target.toString());
        SystemHelper.deleteFile(new File("reformcloud/files/temp/template.zip"));
        return Task.completedTask(null);
    }

    @Override
    public void deployTemplate(@NotNull String group, @NotNull String template, @NotNull Path current, @NotNull Collection<String> collection) {
    }

    @Override
    public void deleteTemplate(@NotNull String group, @NotNull String template) {
    }

    private String getBasePath() {
        return basePath.endsWith("/") ? basePath : basePath + "/";
    }

    @NotNull
    @Override
    public String getName() {
        return "URL";
    }
}
