package systems.reformcloud.reformcloud2.runner.updater;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.function.Consumer;

public final class CloudVersionUpdater {

    private CloudVersionUpdater() {
        throw new UnsupportedOperationException();
    }

    public static String update() {
        if (Files.notExists(Paths.get("reformcloud/.bin/executor.jar"))) {
            return null;
        }

        Properties properties = new Properties();
        openConnection("https://internal.reformcloud.systems/version.properties", inputStream -> {
            try {
                properties.load(inputStream);
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        });
        return handleVersionFetch(properties.getProperty("version"));
    }

    private static String handleVersionFetch(String version) {
        if (version.equals(System.getProperty("reformcloud.runner.version"))) {
            return null;
        }

        boolean fetchRunner = Files.exists(Paths.get("reformcloud/files/runner.jar"));

        System.out.println("New version " + version + " detected! You are running on " + System.getProperty("reformcloud.runner.version"));
        System.out.println("Applying update from reformcloud-central...");

        openConnection("https://internal.reformcloud.systems/executor.jar", inputStream -> {
            try {
                Files.copy(inputStream, Paths.get("reformcloud/.bin/executor.jar"), StandardCopyOption.REPLACE_EXISTING);
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        });

        if (fetchRunner) {
            System.out.println("Applied update to executor.jar, updating runner.jar...");
            openConnection("https://internal.reformcloud.systems/runner.jar", inputStream -> {
                try {
                    Files.copy(inputStream, Paths.get("reformcloud/files/runner.jar"), StandardCopyOption.REPLACE_EXISTING);
                } catch (final IOException ex) {
                    ex.printStackTrace();
                }
            });
        }

        System.out.println("Applied update of version " + version + ". Starting normally...");
        return version;
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
