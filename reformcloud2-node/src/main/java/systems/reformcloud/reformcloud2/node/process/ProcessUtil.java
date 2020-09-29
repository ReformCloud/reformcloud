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
package systems.reformcloud.reformcloud2.node.process;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.io.DownloadHelper;
import systems.reformcloud.reformcloud2.executor.api.io.IOUtils;
import systems.reformcloud.reformcloud2.executor.api.process.api.ProcessInclusion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

final class ProcessUtil {

    private ProcessUtil() {
        throw new UnsupportedOperationException();
    }

    private static final Pattern IP_PATTERN = Pattern.compile(
            "(?<!\\d|\\d\\.)" +
                    "(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])" +
                    "(?!\\d|\\.\\d)"
    );

    static @NotNull Optional<String> uploadLog(@NotNull Collection<String> logLines) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String cachedLogLine : logLines) {
            Matcher matcher = IP_PATTERN.matcher(cachedLogLine);
            if (matcher.find()) {
                String ip = matcher.group();
                stringBuilder.append(cachedLogLine.replace(ip, "***.***.***.***"));
            } else {
                stringBuilder.append(cachedLogLine);
            }

            stringBuilder.append("\n");
        }

        String text = stringBuilder.toString();
        String result = uploadLog0("https://paste.reformcloud.systems", text);
        if (result != null) {
            return Optional.of(result);
        }

        return Optional.ofNullable(uploadLog0("https://just-paste.it", text));
    }

    @Nullable
    private static String uploadLog0(@NotNull String pasteServerUrl, @NotNull String text) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(pasteServerUrl + "/documents").openConnection();
            connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11"
            );
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.connect();

            try (OutputStream stream = connection.getOutputStream()) {
                stream.write(text.getBytes(StandardCharsets.UTF_8));
                stream.flush();
            }

            if (connection.getResponseCode() != 201) {
                return null;
            }

            try (BufferedReader stream = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String lines = stream.lines().collect(Collectors.joining("\n"));
                return pasteServerUrl + '/' + new JsonConfiguration(lines).getString("key");
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    static void loadInclusions(@NotNull Path processPath, @NotNull Collection<ProcessInclusion> inclusions) {
        inclusions.forEach(inclusion -> {
            Path target = Paths.get("reformcloud/files/inclusions", inclusion.getName());
            if (Files.exists(target)) {
                return;
            }

            DownloadHelper.openConnection(
                    inclusion.getUrl(),
                    new HashMap<>(),
                    stream -> {
                        try {
                            IOUtils.createDirectory(target.getParent());
                            Files.copy(stream, target);
                        } catch (final Throwable throwable) {
                            System.err.println("Unable to prepare inclusion " + inclusion.getName() + "!");
                            System.err.println("The error was: " + throwable.getMessage());
                        }
                    }, throwable -> {
                        System.err.println("Unable to open connection to given download server " + inclusion.getUrl());
                        System.err.println("The error was: " + CommonHelper.formatThrowable(throwable));
                    }
            );
        });

        inclusions.forEach(inclusion -> {
            Path path = Paths.get("reformcloud/files/inclusions", inclusion.getName());
            if (Files.notExists(path)) {
                return;
            }

            Path target = Paths.get(processPath.toString(), inclusion.getName());
            try {
                IOUtils.createDirectory(target.getParent());
                Files.copy(path, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}
