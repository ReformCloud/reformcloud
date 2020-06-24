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
package systems.reformcloud.reformcloud2.executor.api.io;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.language.LanguageManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@ApiStatus.Internal
public final class DownloadHelper {

    private DownloadHelper() {
        throw new UnsupportedOperationException();
    }

    public static void downloadAndDisconnect(@NotNull String url, @NotNull String target) {
        openURLConnection(url, new HashMap<>(), connection -> {
            System.out.println(LanguageManager.get("runtime-download-file", url, getSize(connection.getContentLengthLong())));
            long start = System.currentTimeMillis();

            try (InputStream stream = connection.getInputStream()) {
                IOUtils.createDirectory(Paths.get(target).getParent());
                IOUtils.doCopy(stream, Paths.get(target), StandardCopyOption.REPLACE_EXISTING);
            } catch (final IOException ex) {
                ex.printStackTrace();
            }

            System.out.println(LanguageManager.get("runtime-download-file-completed",
                    url, System.currentTimeMillis() - start));
        }, throwable -> throwable.printStackTrace());
    }

    public static void openConnection(@NotNull String url, @NotNull Consumer<InputStream> consumer) {
        openConnection(url, new HashMap<>(), consumer);
    }

    public static void openConnection(@NotNull String url, @NotNull Map<String, String> headers,
                                      @NotNull Consumer<InputStream> inputStreamConsumer) {
        openConnection(url, headers, inputStreamConsumer, ex -> ex.printStackTrace());
    }

    public static void openConnection(@NotNull String url, @NotNull Map<String, String> headers,
                                      @NotNull Consumer<InputStream> inputStreamConsumer,
                                      @NotNull Consumer<Throwable> exceptionConsumer) {
        openURLConnection(url, headers, httpURLConnection -> {
            try (InputStream stream = httpURLConnection.getInputStream()) {
                inputStreamConsumer.accept(stream);
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }, exceptionConsumer);
    }

    public static void openURLConnection(@NotNull String url, @NotNull Map<String, String> headers,
                                         @NotNull Consumer<HttpURLConnection> connectionConsumer,
                                         @NotNull Consumer<Throwable> exceptionConsumer) {
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

    @NotNull
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
