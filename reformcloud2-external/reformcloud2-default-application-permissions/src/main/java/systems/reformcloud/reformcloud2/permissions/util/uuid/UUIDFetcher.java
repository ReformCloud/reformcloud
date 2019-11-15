package systems.reformcloud.reformcloud2.permissions.util.uuid;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class UUIDFetcher {

    private UUIDFetcher() {
        throw new UnsupportedOperationException();
    }

    private static final Pattern PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

    private static final Map<String, UUID> CACHE = new ConcurrentHashMap<>();

    /**
     * Gets a uuid from the given name
     *
     * @param name The name of the player
     * @return The uuid of the player
     */
    public static UUID getUUIDFromName(final String name) {
        if (CACHE.containsKey(name)) {
            return CACHE.get(name);
        }

        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("https://api.minetools.eu/uuid/" + name).openConnection();
            httpURLConnection.setDoOutput(false);
            httpURLConnection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            httpURLConnection.setUseCaches(true);
            httpURLConnection.connect();

            try (InputStreamReader reader = new InputStreamReader(httpURLConnection.getInputStream())) {
                JsonConfiguration configuration = new JsonConfiguration(reader);
                if (configuration.has("id")) {
                    UUID uuid = UUID.fromString(PATTERN.matcher(configuration.getString("id")).replaceAll("$1-$2-$3-$4-$5"));
                    CACHE.put(name, uuid);
                    return uuid;
                }
            }
        } catch (final IOException ignored) {
        }

        return null;
    }
}
