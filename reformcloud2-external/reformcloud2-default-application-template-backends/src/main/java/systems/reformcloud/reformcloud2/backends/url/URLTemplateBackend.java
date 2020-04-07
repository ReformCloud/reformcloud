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

    private URLTemplateBackend(JsonConfiguration configuration) {
        this.basePath = configuration.getString("baseUrl");
    }

    private final String basePath;

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
