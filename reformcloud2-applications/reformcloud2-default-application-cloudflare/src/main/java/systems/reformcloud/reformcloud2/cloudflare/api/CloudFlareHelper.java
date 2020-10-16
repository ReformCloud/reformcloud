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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.cloudflare.config.CloudFlareConfig;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.io.IOUtils;
import systems.reformcloud.reformcloud2.executor.api.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Streams;
import systems.reformcloud.reformcloud2.node.NodeExecutor;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Inet6Address;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    public static boolean init(@NotNull String baseFolder) {
        if (Files.notExists(Paths.get(baseFolder + "/config.json"))) {
            IOUtils.createDirectory(Paths.get(baseFolder));
            new JsonConfiguration()
                .add("config", new CloudFlareConfig(
                    "someone@example.com",
                    "",
                    "example.com",
                    "",
                    "@"
                )).write(Paths.get(baseFolder + "/config.json"));
            return true;
        }

        cloudFlareConfig = JsonConfiguration.read(Paths.get(baseFolder + "/config.json")).get("config", CloudFlareConfig.TYPE_TOKEN);
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

        CACHE.put(target.getProcessDetail().getProcessUniqueID(), dnsID);
    }

    private static String createSRVRecord(ProcessInformation target) {
        if (!A_RECORD_CACHE.containsKey(target.getProcessDetail().getParentName())) {
            String recordID = createRecord(target, prepareARecord(target));
            if (recordID != null) {
                A_RECORD_CACHE.put(target.getProcessDetail().getParentName(), recordID);
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

            try (InputStream stream = httpURLConnection.getResponseCode() < 400
                ? httpURLConnection.getInputStream() : httpURLConnection.getErrorStream()
            ) {
                JsonConfiguration result = new JsonConfiguration(stream);
                httpURLConnection.disconnect();

                if (result.getBoolean("success") && result.has("result")) {
                    return result.get("result").getString("id");
                } else {
                    try {
                        JsonArray array = result.getJsonObject().getAsJsonArray("errors");
                        if (array.size() == 0) {
                            // CloudFlare sent us no errors?
                            return null;
                        }

                        JsonElement first = array.get(0);
                        if (!first.isJsonObject()) {
                            // Should never happen
                            return null;
                        }

                        JsonObject jsonObject = first.getAsJsonObject();
                        if (jsonObject.has("code") && jsonObject.get("code").getAsLong() == 81057) {
                            // The record already exists
                            return null;
                        }

                        if (jsonObject.has("message") && jsonObject.has("code")) {
                            System.err.println(LanguageManager.get(
                                "cloudflare-create-error",
                                configuration.getOrDefault("type", "unknown"),
                                target.getProcessDetail().getName(),
                                jsonObject.get("code").getAsLong(),
                                httpURLConnection.getResponseCode(),
                                jsonObject.get("message").getAsString())
                            );
                            return null;
                        }
                    } catch (final Throwable ignored) {
                    }

                    System.err.println(LanguageManager.get(
                        "cloudflare-create-error",
                        configuration.getOrDefault("type", "unknown"),
                        target.getProcessDetail().getName(),
                        -1,
                        httpURLConnection.getResponseCode(),
                        "No reason provided"
                    ));
                }
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static void handleStop() {
        Streams.forEachValues(A_RECORD_CACHE, CloudFlareHelper::deleteRecord);
        A_RECORD_CACHE.clear();

        for (ProcessInformation shouldHandleProcess : getShouldHandleProcesses()) {
            deleteRecord(shouldHandleProcess);
        }
    }

    public static void deleteRecord(ProcessInformation target) {
        String dnsID = CACHE.remove(target.getProcessDetail().getProcessUniqueID());
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

            try (InputStream stream = httpURLConnection.getResponseCode() < 400
                ? httpURLConnection.getInputStream() : httpURLConnection.getErrorStream()
            ) {
                new JsonConfiguration(stream); // wait for the result
            }

            httpURLConnection.disconnect();
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    private static JsonConfiguration prepareConfig(ProcessInformation target) {
        return new JsonConfiguration()
            .add("type", "SRV")
            .add("name", "_minecraft._tcp." + cloudFlareConfig.getDomainName())
            .add("content", "SRV 1 1 " + target.getNetworkInfo().getPort()
                + " " + target.getProcessDetail().getParentName() + "." + cloudFlareConfig.getDomainName())
            .add("ttl", 1)
            .add("priority", 1)
            .add("proxied", false)
            .add("data", new JsonConfiguration()
                .add("service", "_minecraft")
                .add("proto", "_tcp")
                .add("name", cloudFlareConfig.getSubDomain().equals("@")
                    ? cloudFlareConfig.getDomainName() : cloudFlareConfig.getSubDomain())
                .add("priority", 1)
                .add("weight", 1)
                .add("port", target.getNetworkInfo().getPort())
                .add("target", target.getProcessDetail().getParentName() + "." + cloudFlareConfig.getDomainName())
                .getJsonObject()
            );
    }

    private static JsonConfiguration prepareARecord(ProcessInformation processInformation) {
        return new JsonConfiguration()
            .add("type", processInformation.getNetworkInfo().getHost() instanceof Inet6Address ? "AAAA" : "A")
            .add("name", processInformation.getProcessDetail().getParentName() + "." + cloudFlareConfig.getDomainName())
            .add("content", processInformation.getNetworkInfo().getHostPlain())
            .add("ttl", 1)
            .add("proxied", false)
            .add("data", new JsonConfiguration().getJsonObject());

    }

    @NotNull
    private static Collection<ProcessInformation> getShouldHandleProcesses() {
        return Streams.allOf(
            ExecutorAPI.getInstance().getProcessProvider().getProcesses(),
            CloudFlareHelper::shouldHandle
        );
    }

    public static boolean hasEntry(@NotNull ProcessInformation processInformation) {
        return CACHE.containsKey(processInformation.getProcessDetail().getProcessUniqueID());
    }

    public static boolean shouldHandle(@NotNull ProcessInformation process) {
        return !process.getProcessDetail().getTemplate().isServer()
            && NodeExecutor.getInstance().isOwnIdentity(process.getProcessDetail().getParentName());
    }
}
