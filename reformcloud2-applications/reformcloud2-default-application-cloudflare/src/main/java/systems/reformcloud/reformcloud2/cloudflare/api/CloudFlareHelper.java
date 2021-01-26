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
package systems.reformcloud.reformcloud2.cloudflare.api;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.cloudflare.config.CloudFlareConfig;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.Element;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.types.Array;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.types.Object;
import systems.reformcloud.reformcloud2.executor.api.language.TranslationHolder;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.utility.MoreCollections;
import systems.reformcloud.reformcloud2.node.NodeExecutor;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Inet6Address;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CloudFlareHelper {

  private static final String CLOUD_FLARE_API_URL = "https://api.cloudflare.com/client/v4/";
  private static final Map<UUID, String> CACHE = new ConcurrentHashMap<>();
  private static final Map<String, String> A_RECORD_CACHE = new ConcurrentHashMap<>();
  private static CloudFlareConfig cloudFlareConfig;

  private CloudFlareHelper() {
    throw new UnsupportedOperationException();
  }

  public static boolean init(@NotNull Path configPath) {
    if (Files.notExists(configPath)) {
      JsonConfiguration.newJsonConfiguration()
        .add("config", new CloudFlareConfig(
          "someone@example.com",
          "",
          "example.com",
          "",
          "@"
        )).write(configPath);
      return true;
    }

    cloudFlareConfig = JsonConfiguration.newJsonConfiguration(configPath).get("config", CloudFlareConfig.class);
    return false;
  }

  public static void loadAlreadyRunning() {
    for (ProcessInformation processInformation : getShouldHandleProcesses()) {
      CloudFlareHelper.createForProcess(processInformation);
    }
  }

  public static void createForProcess(@NotNull ProcessInformation target) {
    String dnsID = createSRVRecord(target);
    if (dnsID == null) {
      return;
    }

    CACHE.put(target.getId().getUniqueId(), dnsID);
  }

  private static String createSRVRecord(ProcessInformation target) {
    if (!A_RECORD_CACHE.containsKey(target.getId().getNodeName())) {
      String recordID = createRecord(target, prepareARecord(target));
      if (recordID != null) {
        A_RECORD_CACHE.put(target.getId().getNodeName(), recordID);
      }
    }

    return createRecord(target, prepareConfig(target));
  }

  private static String createRecord(ProcessInformation target, JsonConfiguration configuration) {
    try {
      HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(CLOUD_FLARE_API_URL + "zones/"
        + cloudFlareConfig.getZoneId() + "/dns_records").openConnection();
      httpURLConnection.setRequestMethod("POST");
      httpURLConnection.setDoOutput(true);
      httpURLConnection.setUseCaches(false);
      httpURLConnection.setRequestProperty("X-Auth-Email", cloudFlareConfig.getEmail());
      httpURLConnection.setRequestProperty("X-Auth-Key", cloudFlareConfig.getApiToken());
      httpURLConnection.setRequestProperty("Accept", "application/json");
      httpURLConnection.setRequestProperty("Content-Type", "application/json");

      try (DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream())) {
        dataOutputStream.writeBytes(configuration.toPrettyString());
        dataOutputStream.flush();
      }

      final int responseCode = httpURLConnection.getResponseCode();
      try (InputStream stream = responseCode < 400 ? httpURLConnection.getInputStream() : httpURLConnection.getErrorStream()) {
        JsonConfiguration result = JsonConfiguration.newJsonConfiguration(stream);
        httpURLConnection.disconnect();

        if (result.getBoolean("success") && result.has("result")) {
          return result.get("result").getString("id");
        } else {
          result.getBackingObject()
            .get("errors")
            .filter(Element::isArray)
            .map(Element::getAsArray)
            .ifPresent(array -> {
              if (array.size() == 0) {
                // CloudFlare sent us no errors?
                return;
              }

              array.get(0).ifPresent(first -> {
                Object object = (Object) first;
                if (object.has("code") && object.get("code").map(Element::getAsLong).orElse(0L) == 81057) {
                  // The record already exists
                  return;
                }

                if (object.has("message") && object.has("code")) {
                  System.err.println(TranslationHolder.translate(
                    "cloudflare-create-error",
                    configuration.getOrDefault("type", "unknown"),
                    target.getName(),
                    object.get("code").map(Element::getAsLong).orElse(0L),
                    responseCode,
                    object.get("message").map(Element::getAsString).orElse("no message")
                  ));
                }
              });
            });
          return null;
        }
      }
    } catch (final IOException ex) {
      ex.printStackTrace();
    }

    return null;
  }

  public static void handleStop() {
    MoreCollections.forEachValues(A_RECORD_CACHE, CloudFlareHelper::deleteRecord);
    A_RECORD_CACHE.clear();

    for (ProcessInformation shouldHandleProcess : getShouldHandleProcesses()) {
      deleteRecord(shouldHandleProcess);
    }
  }

  public static void deleteRecord(ProcessInformation target) {
    String dnsID = CACHE.remove(target.getId().getUniqueId());
    if (dnsID == null) {
      return;
    }

    deleteRecord(dnsID);
  }

  private static void deleteRecord(String dnsID) {
    try {
      HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(CLOUD_FLARE_API_URL + "zones/"
        + cloudFlareConfig.getZoneId() + "/dns_records/" + dnsID).openConnection();
      httpURLConnection.setRequestMethod("DELETE");
      httpURLConnection.setUseCaches(false);
      httpURLConnection.setRequestProperty("X-Auth-Email", cloudFlareConfig.getEmail());
      httpURLConnection.setRequestProperty("X-Auth-Key", cloudFlareConfig.getApiToken());
      httpURLConnection.setRequestProperty("Accept", "application/json");
      httpURLConnection.setRequestProperty("Content-Type", "application/json");

      httpURLConnection.disconnect();
    } catch (final IOException ex) {
      ex.printStackTrace();
    }
  }

  private static JsonConfiguration prepareConfig(ProcessInformation target) {
    return JsonConfiguration.newJsonConfiguration()
      .add("type", "SRV")
      .add("name", "_minecraft._tcp." + cloudFlareConfig.getDomainName())
      .add("content", "SRV 1 1 " + target.getHost().getPort()
        + " " + target.getId().getNodeName() + "." + cloudFlareConfig.getDomainName())
      .add("ttl", 1)
      .add("priority", 1)
      .add("proxied", false)
      .add("data", JsonConfiguration.newJsonConfiguration()
        .add("service", "_minecraft")
        .add("proto", "_tcp")
        .add("name", cloudFlareConfig.getSubDomain().equals("@") ? cloudFlareConfig.getDomainName() : cloudFlareConfig.getSubDomain())
        .add("priority", 1)
        .add("weight", 1)
        .add("port", target.getHost().getPort())
        .add("target", target.getId().getNodeName() + "." + cloudFlareConfig.getDomainName())
        .getBackingObject()
      );
  }

  private static JsonConfiguration prepareARecord(ProcessInformation processInformation) {
    return JsonConfiguration.newJsonConfiguration()
      .add("type", processInformation.getHost().toInetAddress() instanceof Inet6Address ? "AAAA" : "A")
      .add("name", processInformation.getId().getNodeName() + "." + cloudFlareConfig.getDomainName())
      .add("content", processInformation.getHost().getHost())
      .add("ttl", 1)
      .add("proxied", false)
      .add("data", JsonConfiguration.newJsonConfiguration().getBackingObject());
  }

  @NotNull
  private static Collection<ProcessInformation> getShouldHandleProcesses() {
    return MoreCollections.allOf(
      ExecutorAPI.getInstance().getProcessProvider().getProcesses(),
      CloudFlareHelper::shouldHandle
    );
  }

  public static boolean hasEntry(@NotNull ProcessInformation processInformation) {
    return CACHE.containsKey(processInformation.getId().getUniqueId());
  }

  public static boolean shouldHandle(@NotNull ProcessInformation process) {
    return process.getPrimaryTemplate().getVersion().getVersionType().isProxy()
      && NodeExecutor.getInstance().isOwnIdentity(process.getId().getNodeName());
  }
}
