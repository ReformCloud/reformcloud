package systems.reformcloud.reformcloud2.permissions.util.uuid;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.permissions.util.basic.DefaultPermissionUtil;

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
                JsonConfiguration configuration = new JsonConfiguration(reader);
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

    private static UUID fromDatabase(String name) {
        JsonConfiguration configuration = ExecutorAPI.getInstance().find(
                DefaultPermissionUtil.PERMISSION_NAME_TO_UNIQUE_ID_TABLE,
                name,
                null
        );
        return configuration == null || !configuration.has("id") ? null : configuration.get("id", UUID.class);
    }

    private static UUID fromString(String name) throws IllegalArgumentException {
        String[] components = name.split("-");
        if (components.length != 5) {
            throw new IllegalArgumentException("Invalid UUID string: " + name);
        }

        for (int i=0; i<5; i++) {
            components[i] = "0x"+components[i];
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
