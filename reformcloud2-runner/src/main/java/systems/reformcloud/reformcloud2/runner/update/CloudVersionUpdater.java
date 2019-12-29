package systems.reformcloud.reformcloud2.runner.update;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.function.Consumer;

public class CloudVersionUpdater {

    public static void update() {
        if (Files.notExists(Paths.get("reformcloud/.bin/executor.jar"))) {
            return;
        }

        openConnection("https://internal.reformcloud.systems/version.properties", inputStream -> {
            try {
                Properties properties = new Properties();
                properties.load(inputStream);

                handleVersionFetch(properties.getProperty("version"));
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    private static void handleVersionFetch(String version) {
        if (version.equals(System.getProperty("reformcloud.runner.version"))) {
            return;
        }

        System.out.println("New version " + version + " detected! You are running on " + System.getProperty("reformcloud.runner.version"));
        System.out.println("Applying update from reformcloud-central...");

        openConnection("https://internal.reformcloud.systems/executor.jar", inputStream -> {
            try {
                Files.copy(inputStream, Paths.get("reformcloud/.bin/executor.jar"), StandardCopyOption.REPLACE_EXISTING);
            } catch (final IOException ex) {
                ex.printStackTrace();
            }

            System.out.println("Applied update of version " + version + ". Starting normally...");
        });
    }

    private static void openConnection(String url, Consumer<InputStream> inputStreamConsumer) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11"
            );
            httpURLConnection.setDoOutput(false);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.connect();

            try (InputStream inputStream = httpURLConnection.getInputStream()) {
                inputStreamConsumer.accept(inputStream);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
}
