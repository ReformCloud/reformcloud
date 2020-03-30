package systems.reformcloud.reformcloud2.cloudflare.api;

import com.google.gson.JsonArray;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.cloudflare.config.CloudFlareConfig;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CloudFlareHelper {

    private CloudFlareHelper() {
        throw new UnsupportedOperationException();
    }

    private static final String CLOUD_FLARE_API_URL = "https://api.cloudflare.com/client/v4/";

    private static final Map<UUID, String> CACHE = new ConcurrentHashMap<>();

    private static final Map<String, String> A_RECORD_CACHE = new ConcurrentHashMap<>();

    private static CloudFlareConfig cloudFlareConfig;

    public static boolean init(@NotNull String baseFolder) {

        if (Files.notExists(Paths.get(baseFolder + "/config.json"))) {
            SystemHelper.createDirectory(Paths.get(baseFolder));
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
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getAllProcesses()
                .stream()
                .filter(e -> !e.getProcessDetail().getTemplate().isServer())
                .forEach(CloudFlareHelper::createForProcess);
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

                        if (array.get(0).getAsJsonObject().get("code").getAsLong() == 81057) {
                            // The record already exists
                            return null;
                        }
                    } catch (final Throwable ignored) {
                    }

                    System.err.println(LanguageManager.get("cloudlfare-create-error", target.getProcessDetail().getName()));
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
                .add("type", "A")
                .add("name", processInformation.getProcessDetail().getParentName() + "." + cloudFlareConfig.getDomainName())
                .add("content", processInformation.getNetworkInfo().getHost())
                .add("ttl", 1)
                .add("proxied", false)
                .add("data", new JsonConfiguration().getJsonObject());

    }
}
