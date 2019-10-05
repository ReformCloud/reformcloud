package systems.reformcloud.reformcloud2.executor.api.common.utility.system;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class DownloadHelper {

    public static void downloadAndDisconnect(String url, String target) {
        openConnection(url, stream -> {
            SystemHelper.createDirectory(Paths.get(target).getParent());
            SystemHelper.doCopy(stream, Paths.get(target), StandardCopyOption.REPLACE_EXISTING);
        });
    }

    public static void openConnection(String url, Map<String, String> headers, Consumer<InputStream> inputStreamConsumer) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11"
            );
            headers.forEach(httpURLConnection::setRequestProperty);
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

    public static void openConnection(String url, Consumer<InputStream> consumer) {
        openConnection(url, new HashMap<>(), consumer);
    }
}
