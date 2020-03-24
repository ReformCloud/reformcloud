package systems.reformcloud.reformcloud2.executor.api.common.utility;

import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

public final class StringUtil {

    public static final String RUNNER_DOWNLOAD_URL = "https://internal.reformcloud.systems/runner.jar";

    public static final String NULL_PATH = new File("reformcloud/.bin/dev/null").getAbsolutePath();

    @Nonnull
    public static String generateString(int times) {
        Conditions.isTrue(times > 0);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < times; i++) {
            stringBuilder.append(UUID.randomUUID().toString().replace("-", ""));
        }

        return stringBuilder.toString();
    }

    @Nonnull
    public static String getConsolePrompt() {
        return LanguageManager.get("logger.console.prompt")
                .replace("%version%", System.getProperty("reformcloud.runner.version", "c-build"))
                .replace("%user_name%", System.getProperty("user.name", "unknown")) + " ";
    }

    public static String formatError(@Nonnull String error) {
        return String.format("Unable to process action %s. Please report this DIRECTLY to reformcloud it is a fatal error", error);
    }

    @Nonnull
    public static Properties calcProperties(@Nonnull String[] strings, int from) {
        Properties properties = new Properties();
        if (strings.length < from) {
            return properties;
        }

        String[] copy = Arrays.copyOfRange(strings, from, strings.length);
        for (String string : copy) {
            if (!string.startsWith("--") && !string.contains("=")) {
                continue;
            }

            String[] split = string.replaceFirst("--", "").split("=");
            if (split.length != 2) {
                continue;
            }

            properties.setProperty(split[0], split[1]);
        }

        return properties;
    }
}
