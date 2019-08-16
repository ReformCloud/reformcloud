package de.klaro.reformcloud2.executor.api.common.utility.system;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public final class DownloadHelper {

    public static void downloadAndDisconnect(String url, String target) {
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
                if (!Files.exists(Paths.get(target).getParent())) {
                    Files.createDirectories(Paths.get(target).getParent());
                }

                Files.copy(inputStream, Paths.get(target), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
}
