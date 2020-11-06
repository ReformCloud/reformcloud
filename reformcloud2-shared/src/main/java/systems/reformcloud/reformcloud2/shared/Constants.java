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
package systems.reformcloud.reformcloud2.shared;

import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.IntStream;

@ApiStatus.Internal
public final class Constants {

    public static final String EMPTY_STRING = "";
    public static final long MEGABYTE = 1024 * 1024;
    public static final String RUNNER_DOWNLOAD_URL = "https://internal.reformcloud.systems/runner.jar";
    public static final String DEV_NULL_PATH = Paths.get("reformcloud/.bin/dev/null").toAbsolutePath().toString();
    public static final ExecutorService CACHED_THREAD_POOL = Executors.newCachedThreadPool();
    public static final ScheduledExecutorService SINGLE_THREAD_SCHEDULED_EXECUTOR = Executors.newSingleThreadScheduledExecutor();
    public static final ArrayDeque<String> EMPTY_STRING_QUEUE = new ArrayDeque<>();
    public static final DateFormat FULL_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy kk:mm:ss");
    public static final DecimalFormat TWO_POINT_THREE_DECIMAL_FORMAT = new DecimalFormat("##.###");
    public static final Character[] NUMBERS_AND_LETTERS = IntStream.range(48, 123)
        .filter(i -> Character.isDigit(i) || Character.isLetter(i))
        .mapToObj(i -> (char) i)
        .toArray(Character[]::new);

    private Constants() {
        throw new UnsupportedOperationException();
    }
}
