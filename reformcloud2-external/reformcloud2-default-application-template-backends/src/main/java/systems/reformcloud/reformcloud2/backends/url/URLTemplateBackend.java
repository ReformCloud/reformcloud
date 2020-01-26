package systems.reformcloud.reformcloud2.backends.url;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.TemplateBackend;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.TemplateBackendManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.DownloadHelper;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class URLTemplateBackend implements TemplateBackend {

    public static void load(String basePath) {
        if (!Files.exists(Paths.get(basePath + "/url.json"))) {
            new JsonConfiguration()
                    .add("baseUrl", "https://workupload.com/")
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
    public boolean existsTemplate(String group, String template) {
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
    public void createTemplate(String group, String template) {
    }

    @Override
    public void loadTemplate(String group, String template, Path target) {
        DownloadHelper.downloadAndDisconnect(getBasePath() + group + "-" + template + ".zip", target.toString());
    }

    @Override
    public void loadGlobalTemplates(ProcessGroup group, Path target) {
        Streams.allOf(group.getTemplates(), e -> e.getBackend().equals(getName())
                && e.isGlobal()).forEach(e -> this.loadTemplate(group.getName(), e.getName(), target));
    }

    @Override
    public void deployTemplate(String group, String template, Path current) {
    }

    @Override
    public void deleteTemplate(String group, String template) {
    }

    private String getBasePath() {
        return basePath.endsWith("/") ? basePath : basePath + "/";
    }

    @Nonnull
    @Override
    public String getName() {
        return "URL";
    }
}
