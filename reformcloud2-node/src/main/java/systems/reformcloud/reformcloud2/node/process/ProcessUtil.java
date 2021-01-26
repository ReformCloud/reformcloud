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
import systems.reformcloud.reformcloud2.executor.api.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.process.builder.ProcessInclusion;
import systems.reformcloud.reformcloud2.shared.io.DownloadHelper;
import systems.reformcloud.reformcloud2.shared.io.IOUtils;
import systems.reformcloud.reformcloud2.shared.parser.Parsers;

import java.io.IOException;
import java.io.InputStream;
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

final class ProcessUtil {

  private static final String IP_REPLACEMENT = "ip_address";
  // https://regex101.com/r/vO55z5/1
  private static final Pattern IPV4_PATTERN = Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
  // https://regex101.com/r/vO55z5/2
  private static final Pattern IPV6_STD_PATTERN = Pattern.compile("^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");
  // https://regex101.com/r/vO55z5/3
  private static final Pattern IPV6_HEX_COMPRESSED_PATTERN = Pattern.compile("^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)$");
  // https://regex101.com/r/vO55z5/4
  private static final Pattern IPV6_MIXED_COMPRESSED_PATTERN = Pattern.compile("^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}:(?:[0-9A-Fa-f]{1,4}:)*)?)$");

  private ProcessUtil() {
    throw new UnsupportedOperationException();
  }

  static @NotNull Optional<String> uploadLog(@NotNull Collection<String> logLines) {
    StringBuilder stringBuilder = new StringBuilder();
    for (String cachedLogLine : logLines) {
      stringBuilder.append(replaceContainedIps(cachedLogLine)).append("\n");
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

      try (InputStream stream = connection.getInputStream()) {
        return pasteServerUrl + '/' + JsonConfiguration.newJsonConfiguration(stream).getString("key");
      }
    } catch (final IOException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  @NotNull
  private static String replaceContainedIps(@NotNull String line) {
    Matcher ipv4 = IPV4_PATTERN.matcher(line);
    if (ipv4.find()) {
      return line.replace(ipv4.group(), IP_REPLACEMENT);
    }

    Matcher ipv6Std = IPV6_STD_PATTERN.matcher(line);
    if (ipv6Std.find()) {
      return line.replace(ipv6Std.group(), IP_REPLACEMENT);
    }

    Matcher ipv6HexCompressed = IPV6_HEX_COMPRESSED_PATTERN.matcher(line);
    if (ipv6HexCompressed.find()) {
      return line.replace(ipv6HexCompressed.group(), IP_REPLACEMENT);
    }

    Matcher ipv6MixedCompressed = IPV6_MIXED_COMPRESSED_PATTERN.matcher(line);
    if (ipv6MixedCompressed.find()) {
      return line.replace(ipv6MixedCompressed.group(), IP_REPLACEMENT);
    }

    return line;
  }

  static void loadInclusions(@NotNull Path processPath, @NotNull Collection<ProcessInclusion> inclusions) {
    inclusions.forEach(inclusion -> {
      Path target = Paths.get("reformcloud/files/inclusions", inclusion.getName());
      if (Files.exists(target)) {
        return;
      }

      DownloadHelper.connect(
        inclusion.getDownloadUrl(),
        new HashMap<>(),
        (connection, exception) -> {
          if (exception != null) {
            System.err.println("Unable to open connection to given download server " + inclusion.getDownloadUrl());
            System.err.println("The error was: " + Parsers.EXCEPTION_FORMAT.parse(exception));
            return;
          }

          try {
            IOUtils.createDirectory(target.getParent());
            Files.copy(connection.getInputStream(), target);
          } catch (final Throwable throwable) {
            System.err.println("Unable to prepare inclusion " + inclusion.getName() + "!");
            System.err.println("The error was: " + throwable.getMessage());
          }
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
