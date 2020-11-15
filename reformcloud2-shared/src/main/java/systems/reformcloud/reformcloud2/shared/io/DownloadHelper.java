/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.reformcloud.reformcloud2.shared.io;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.language.TranslationHolder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@ApiStatus.Internal
public final class DownloadHelper {

    private DownloadHelper() {
        throw new UnsupportedOperationException();
    }

    public static void download(@NotNull String url, @NotNull String target) {
        connect(url, (connection, exception) -> {
            if (connection != null && connection.getResponseCode() == 200) {
                System.out.println(TranslationHolder.translate("runtime-download-file", url, getSize(connection.getContentLengthLong())));
                final long startMillis = System.currentTimeMillis();

                try (InputStream stream = connection.getInputStream()) {
                    IOUtils.createDirectory(Path.of(target).getParent());
                    IOUtils.doCopy(stream, Path.of(target), StandardCopyOption.REPLACE_EXISTING);
                } catch (final IOException ex) {
                    ex.printStackTrace();
                }

                System.out.println(TranslationHolder.translate("runtime-download-file-completed", url, System.currentTimeMillis() - startMillis));
            } else {
                new RuntimeException("Unable to download from " + url, exception).printStackTrace();
            }
        });
    }

    public static void connect(@NotNull String url, @NotNull DownloadCallback callback) {
        connect(url, Map.of(), callback);
    }

    public static void connect(@NotNull String url, @NotNull Map<String, String> headers, @NotNull DownloadCallback callback) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            httpURLConnection.setDoOutput(false);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(5000);
            headers.forEach(httpURLConnection::setRequestProperty);
            httpURLConnection.connect();

            try {
                callback.handleConnection(httpURLConnection, null);
            } finally {
                httpURLConnection.disconnect();
            }
        } catch (final Throwable throwable) {
            try {
                callback.handleConnection(null, throwable);
            } catch (Throwable t) {
                throw new RuntimeException("Unable to post exception to listener", t);
            }
        }
    }

    @NotNull
    private static String getSize(long size) {
        if (size >= 1024) {
            int z = (63 - Long.numberOfLeadingZeros(size)) / 10;
            return String.format("%.1f %sB", (double) size / (1L << (z * 10)), " KMGTPE".charAt(z));
        }
        return size + " B";
    }
}
