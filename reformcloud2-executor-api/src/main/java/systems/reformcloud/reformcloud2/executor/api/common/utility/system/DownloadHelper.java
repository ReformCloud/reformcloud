package systems.reformcloud.reformcloud2.executor.api.common.utility.system;

import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;

import javax.annotation.Nonnull;
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

    private DownloadHelper() {
        throw new UnsupportedOperationException();
    }

    public static void downloadAndDisconnect(@Nonnull String url, @Nonnull String target) {
        openURLConnection(url, new HashMap<>(), connection -> {
            System.out.println(LanguageManager.get("runtime-download-file", url, getSize(connection.getContentLengthLong())));
            long start = System.currentTimeMillis();

            try (InputStream stream = connection.getInputStream()) {
                SystemHelper.createDirectory(Paths.get(target).getParent());
                SystemHelper.doCopy(stream, Paths.get(target), StandardCopyOption.REPLACE_EXISTING);
            } catch (final IOException ex) {
                ex.printStackTrace();
            }

            System.out.println(LanguageManager.get("runtime-download-file-completed",
                    url, System.currentTimeMillis() - start));
        }, throwable -> throwable.printStackTrace());
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
        openURLConnection(url, headers, httpURLConnection -> {
            try (InputStream stream = httpURLConnection.getInputStream()) {
                inputStreamConsumer.accept(stream);
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }, exceptionConsumer);
    }

    public static void openURLConnection(@Nonnull String url, @Nonnull Map<String, String> headers,
                                         @Nonnull Consumer<HttpURLConnection> connectionConsumer,
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
            connectionConsumer.accept(httpURLConnection);
            httpURLConnection.disconnect();
        } catch (final Throwable throwable) {
            exceptionConsumer.accept(throwable);
        }
    }

    @Nonnull
    private static String getSize(long size) {
        if (size >= 1048576L) {
            return Math.round((float) size / 1048576.0F) + "MB";
        }

        if (size >= 1024) {
            return Math.round((float) size / 1024.0F) + "KB";
        }

        return Math.round(size) + "B";
    }
}
