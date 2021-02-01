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
package systems.reformcloud.cloudflare.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.cloudflare.config.CloudFlareConfig;
import systems.reformcloud.configuration.JsonConfiguration;
import systems.reformcloud.configuration.json.Element;
import systems.reformcloud.configuration.json.types.Object;
import systems.reformcloud.group.process.ProcessGroup;
import systems.reformcloud.language.TranslationHolder;
import systems.reformcloud.node.NodeExecutor;
import systems.reformcloud.process.ProcessInformation;
import systems.reformcloud.utility.MoreCollections;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Inet6Address;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class CloudFlareHelper {

  private static final String CLOUD_FLARE_API_URL = "https://api.cloudflare.com/client/v4/";
  private static final String ZONE_DNS_URL = CLOUD_FLARE_API_URL + "zones/%s/dns_records/";
  private static final String ZONE_SPECIFIC_DNS_URL = ZONE_DNS_URL + "%s";

  private static final Map<UUID, String> SRV_RECORD_CACHE = new ConcurrentHashMap<>();
  private static final Map<String, String> A_RECORD_CACHE = new ConcurrentHashMap<>();

  private CloudFlareHelper() {
    throw new UnsupportedOperationException();
  }

  @Nullable
  public static CloudFlareConfig init(@NotNull Path configPath) {
    if (Files.notExists(configPath)) {
      JsonConfiguration.newJsonConfiguration()
        .add("config", new CloudFlareConfig(Collections.singleton(new CloudFlareConfig.Entry(
          "someone@example.com",
          "",
          "example.com",
          "",
          "@",
          Collections.emptySet()
        )))).write(configPath);
      return null;
    }

    return JsonConfiguration.newJsonConfiguration(configPath).get("config", CloudFlareConfig.class);
  }

  public static void loadAlreadyRunning(@NotNull CloudFlareConfig config) {
    for (ProcessInformation processInformation : getShouldHandleProcesses()) {
      CloudFlareHelper.createForProcess(processInformation, config);
    }
  }

  public static void createForProcess(@NotNull ProcessInformation target, @NotNull CloudFlareConfig config) {
    createForProcess0(target, selectEntries(target.getProcessGroup(), config));
  }

  private static void createForProcess0(@NotNull ProcessInformation target, @NotNull Set<CloudFlareConfig.Entry> entries) {
    for (CloudFlareConfig.Entry entry : entries) {
      String dnsID = createSRVRecord(target, entry);
      if (dnsID != null) {
        SRV_RECORD_CACHE.put(target.getId().getUniqueId(), dnsID);
      }
    }
  }

  private static String createSRVRecord(@NotNull ProcessInformation target, @NotNull CloudFlareConfig.Entry entry) {
    A_RECORD_CACHE.computeIfAbsent(target.getId().getNodeName(), nodeName -> createRecord(target, prepareARecord(target, entry), entry));
    return createRecord(target, prepareSrvRecord(target, entry), entry);
  }

  private static String createRecord(@NotNull ProcessInformation target, @NotNull JsonConfiguration configuration, @NotNull CloudFlareConfig.Entry entry) {
    try {
      HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(String.format(ZONE_DNS_URL, entry.getZoneId())).openConnection();
      httpURLConnection.setRequestMethod("POST");
      httpURLConnection.setDoOutput(true);
      httpURLConnection.setUseCaches(false);
      httpURLConnection.setRequestProperty("X-Auth-Email", entry.getEmail());
      httpURLConnection.setRequestProperty("X-Auth-Key", entry.getApiToken());
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

  public static void handleStop(@NotNull CloudFlareConfig cloudFlareConfig) {
    for (Map.Entry<String, String> entry : A_RECORD_CACHE.entrySet()) {
      deleteRecord(entry.getValue(), cloudFlareConfig.getEntries());
      A_RECORD_CACHE.remove(entry.getKey());
    }

    for (ProcessInformation shouldHandleProcess : getShouldHandleProcesses()) {
      deleteRecord(shouldHandleProcess, cloudFlareConfig);
    }
  }

  public static void deleteRecord(@NotNull ProcessInformation target, @NotNull CloudFlareConfig config) {
    final String dnsId = SRV_RECORD_CACHE.remove(target.getId().getUniqueId());
    if (dnsId != null) {
      deleteRecord(dnsId, selectEntries(target.getProcessGroup(), config));
    }
  }

  private static void deleteRecord(String dnsId, Set<CloudFlareConfig.Entry> entries) {
    for (CloudFlareConfig.Entry entry : entries) {
      deleteRecord(dnsId, entry);
    }
  }

  private static void deleteRecord(@NotNull String dnsId, @NotNull CloudFlareConfig.Entry entry) {
    try {
      HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(String.format(ZONE_SPECIFIC_DNS_URL, entry.getZoneId(), dnsId)).openConnection();
      httpURLConnection.setRequestMethod("DELETE");
      httpURLConnection.setUseCaches(false);
      httpURLConnection.setRequestProperty("X-Auth-Email", entry.getEmail());
      httpURLConnection.setRequestProperty("X-Auth-Key", entry.getApiToken());
      httpURLConnection.setRequestProperty("Accept", "application/json");
      httpURLConnection.setRequestProperty("Content-Type", "application/json");

      httpURLConnection.disconnect();
    } catch (final IOException ex) {
      ex.printStackTrace();
    }
  }

  @NotNull
  private static JsonConfiguration prepareSrvRecord(@NotNull ProcessInformation target, @NotNull CloudFlareConfig.Entry entry) {
    return JsonConfiguration.newJsonConfiguration()
      .add("type", "SRV")
      .add("name", "_minecraft._tcp." + entry.getDomainName())
      .add("content", "SRV 1 1 " + target.getHost().getPort()
        + " " + target.getId().getNodeName() + "." + entry.getDomainName())
      .add("ttl", 1)
      .add("priority", 1)
      .add("proxied", false)
      .add("data", JsonConfiguration.newJsonConfiguration()
        .add("service", "_minecraft")
        .add("proto", "_tcp")
        .add("name", entry.getSubDomain().equals("@") ? entry.getDomainName() : entry.getSubDomain())
        .add("priority", 1)
        .add("weight", 1)
        .add("port", target.getHost().getPort())
        .add("target", target.getId().getNodeName() + "." + entry.getDomainName())
        .getBackingObject()
      );
  }

  @NotNull
  private static JsonConfiguration prepareARecord(@NotNull ProcessInformation processInformation, @NotNull CloudFlareConfig.Entry entry) {
    return JsonConfiguration.newJsonConfiguration()
      .add("type", processInformation.getHost().toInetAddress() instanceof Inet6Address ? "AAAA" : "A")
      .add("name", processInformation.getId().getNodeName() + "." + entry.getDomainName())
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

  @NotNull
  private static Set<CloudFlareConfig.Entry> selectEntries(@NotNull ProcessGroup group, @NotNull CloudFlareConfig config) {
    return config.getEntries()
      .stream()
      .filter(entry -> entry.getTargetProxyGroups().isEmpty() || entry.getTargetProxyGroups().contains(group.getName()))
      .collect(Collectors.toSet());
  }

  public static boolean hasEntry(@NotNull ProcessInformation processInformation) {
    return SRV_RECORD_CACHE.containsKey(processInformation.getId().getUniqueId());
  }

  public static boolean shouldHandle(@NotNull ProcessInformation process) {
    return process.getPrimaryTemplate().getVersion().getVersionType().isProxy()
      && NodeExecutor.getInstance().isOwnIdentity(process.getId().getNodeName());
  }
}
