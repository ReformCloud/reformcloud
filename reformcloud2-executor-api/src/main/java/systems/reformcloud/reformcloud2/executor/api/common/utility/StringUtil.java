/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
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
package systems.reformcloud.reformcloud2.executor.api.common.utility;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

public final class StringUtil {

    public static final String RUNNER_DOWNLOAD_URL = "https://internal.reformcloud.systems/runner.jar";

    public static final String NULL_PATH = new File("reformcloud/.bin/dev/null").getAbsolutePath();

    @NotNull
    public static String generateString(int times) {
        Conditions.isTrue(times > 0);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < times; i++) {
            stringBuilder.append(UUID.randomUUID().toString().replace("-", ""));
        }

        return stringBuilder.toString();
    }

    @NotNull
    public static String getConsolePrompt() {
        return LanguageManager.get("logger.console.prompt")
                .replace("%version%", System.getProperty("reformcloud.runner.version", "c-build"))
                .replace("%user_name%", System.getProperty("user.name", "unknown")) + " ";
    }

    public static String formatError(@NotNull String error) {
        return String.format("Unable to process action %s. Please report this DIRECTLY to reformcloud it is a fatal error", error);
    }

    @NotNull
    public static Properties calcProperties(@NotNull String[] strings, int from) {
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
