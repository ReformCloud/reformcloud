package systems.reformcloud.reformcloud2.executor.api.common.utility.system;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class DownloadHelper {

    private DownloadHelper() {
        throw new UnsupportedOperationException();
    }

    public static void downloadAndDisconnect(@Nonnull String url, @Nonnull String target) {
        openConnection(url, stream -> {
            SystemHelper.createDirectory(Paths.get(target).getParent());
            SystemHelper.doCopy(stream, Paths.get(target), StandardCopyOption.REPLACE_EXISTING);
        });
    }

    public static void openConnection(@Nonnull String url, @Nonnull Consumer<InputStream> consumer) {
        openConnection(url, new HashMap<>(), consumer);
    }

    public static void openConnection(@Nonnull String url, @Nonnull Map<String, String> headers,
                                      @Nonnull Consumer<InputStream> inputStreamConsumer) {
        openConnection(url, headers, inputStreamConsumer, ex -> ex.printStackTrace());
    }

    public static void openConnection(@Nonnull String url, @Nonnull Map<String, String> headers,
                                      @Nonnull Consumer<InputStream> inputStreamConsumer,
                                      @Nonnull Consumer<Throwable> exceptionConsumer) {
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

            httpURLConnection.disconnect();
        } catch (final Throwable throwable) {
            exceptionConsumer.accept(throwable);
        }
    }
}
