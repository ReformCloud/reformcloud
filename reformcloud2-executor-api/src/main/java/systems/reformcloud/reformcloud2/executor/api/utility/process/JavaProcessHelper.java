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
package systems.reformcloud.reformcloud2.executor.api.utility.process;

import org.jetbrains.annotations.NonNls;
import systems.reformcloud.reformcloud2.executor.api.base.Conditions;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public final class JavaProcessHelper {

    private JavaProcessHelper() {
        throw new UnsupportedOperationException();
    }

    public static void shutdown(Process process, boolean force, boolean await, long timeOut, @NonNls String... shutdownCommands) {
        if (process == null) {
            return;
        }

        Conditions.isTrue(timeOut > 0);

        if (!process.isAlive()) {
            return;
        }

        try {
            OutputStream outputStream = process.getOutputStream();
            Arrays.stream(shutdownCommands).forEach(e -> {
                try {
                    outputStream.write(e.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                } catch (final IOException ignored) {
                    // Ignore - pipe may be closed already
                }
            });

            if (process.waitFor(5, TimeUnit.SECONDS)) {
                process.exitValue();
                return;
            }
        } catch (final Throwable ignored) {
        }

        if (force) {
            process.destroyForcibly();
        } else {
            process.destroy();
        }

        try {
            return;
        } catch (final Throwable throwable) {
            process.destroyForcibly();
        }

        try {
            process.exitValue();
        } catch (final Throwable throwable) {
            if (await) {
                boolean closed = false;
                long end = System.currentTimeMillis() + timeOut;

                while (!closed) {
                    if (System.currentTimeMillis() > end) {
                        break;
                    }

                    try {
                        process.exitValue();
                        closed = true;
                    } catch (final Throwable throwable1) {
                        process.destroyForcibly();

                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
            }
        }
    }
}
