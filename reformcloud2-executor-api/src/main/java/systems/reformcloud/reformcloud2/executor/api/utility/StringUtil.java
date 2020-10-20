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
package systems.reformcloud.reformcloud2.executor.api.utility;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.utility.random.ThreadLocalFastRandom;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.IntStream;

public final class StringUtil {

    public static final String EMPTY = "";
    public static final String RUNNER_DOWNLOAD_URL = "https://internal.reformcloud.systems/runner.jar";
    public static final String NULL_PATH = Paths.get("reformcloud/.bin/dev/null").toAbsolutePath().toString();

    private static final Character[] CHARACTERS = IntStream.range(48, 123).filter(i -> Character.isDigit(i) || Character.isLetter(i)).mapToObj(i -> (char) i).toArray(Character[]::new);

    @NotNull
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.10.3")
    public static String generateString(int times) {
        return generateRandomString(times * 32);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static String generateRandomString(int length) {
        Conditions.isTrue(length > 0);

        StringBuilder stringBuffer = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            stringBuffer.append(CHARACTERS[ThreadLocalFastRandom.current().nextInt(CHARACTERS.length)]);
        }

        return stringBuffer.toString();
    }

    @NotNull
    @Contract(pure = true)
    public static String replaceLastEmpty(@NotNull String text, @NotNull String regex) {
        return replaceLast(text, regex, EMPTY);
    }

    @NotNull
    @Contract(pure = true)
    public static String replaceLast(@NotNull String text, @NotNull String regex, @NotNull String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static Properties calcProperties(@NonNls String[] strings, int from) {
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

            properties.setProperty(split[0].toLowerCase(), split[1]);
        }

        return properties;
    }
}
