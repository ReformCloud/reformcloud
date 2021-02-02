/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
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
package systems.reformcloud.permissions.internal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.configuration.JsonConfiguration;
import systems.reformcloud.permissions.defaults.DefaultPermissionManagement;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public final class UniqueIdFetcher {

  private static final Pattern PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");
  private static final Map<String, UUID> CACHE = new ConcurrentHashMap<>();

  private UniqueIdFetcher() {
    throw new UnsupportedOperationException();
  }

  /**
   * Gets a uuid from the given name
   *
   * @param name The name of the player
   * @return The uuid of the player
   */
  @Nullable
  public static UUID getUUIDFromName(@NotNull String name) {
    if (CACHE.containsKey(name)) {
      return CACHE.get(name);
    }

    UUID database = fromDatabase(name);
    if (database != null) {
      return database;
    }

    try {
      HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("https://api.minetools.eu/uuid/" + name).openConnection();
      httpURLConnection.setDoOutput(false);
      httpURLConnection.setRequestProperty("User-Agent",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
      httpURLConnection.setUseCaches(true);
      httpURLConnection.connect();

      try (InputStreamReader reader = new InputStreamReader(httpURLConnection.getInputStream())) {
        JsonConfiguration configuration = JsonConfiguration.newJsonConfiguration(reader);
        if (configuration.has("id")) {
          try {
            UUID uuid = fromString(PATTERN.matcher(configuration.getString("id")).replaceAll("$1-$2-$3-$4-$5"));
            CACHE.put(name, uuid);
            return uuid;
          } catch (final IllegalArgumentException ex) {
            return null;
          }
        }
      }
    } catch (final IOException ignored) {
    }

    return null;
  }

  @Nullable
  private static UUID fromDatabase(@NotNull String name) {
    Optional<JsonConfiguration> configuration = ExecutorAPI.getInstance().getDatabaseProvider().getDatabase(DefaultPermissionManagement.PERMISSION_NAME_TO_UNIQUE_ID_TABLE).get(
      name,
      ""
    );

    return !configuration.isPresent() || !configuration.get().has("id") ? null : configuration.get().get("id", UUID.class);
  }

  @NotNull
  private static UUID fromString(@NotNull String name) throws IllegalArgumentException {
    String[] components = name.split("-");
    if (components.length != 5) {
      throw new IllegalArgumentException("Invalid UUID string: " + name);
    }

    for (int i = 0; i < 5; i++) {
      components[i] = "0x" + components[i];
    }

    long mostSigBits = Long.decode(components[0]);
    mostSigBits <<= 16;
    mostSigBits |= Long.decode(components[1]);
    mostSigBits <<= 16;
    mostSigBits |= Long.decode(components[2]);

    long leastSigBits = Long.decode(components[3]);
    leastSigBits <<= 48;
    leastSigBits |= Long.decode(components[4]);

    return new UUID(mostSigBits, leastSigBits);
  }
}
