package systems.reformcloud.reformcloud2.executor.api.common.utility.process;

import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public final class JavaProcessHelper {

    private JavaProcessHelper() {
        throw new UnsupportedOperationException();
    }

    public static int shutdown(Process process, boolean force, boolean await, long timeOut, String... shutdownCommands) {
        if (process == null) {
            return -1;
        }

        Conditions.isTrue(timeOut > 0);

        if (!process.isAlive()) {
            return process.exitValue();
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
                return process.exitValue();
            }
        } catch (final Throwable ignored) {
        }

        if (force) {
            process.destroyForcibly();
        } else {
            process.destroy();
        }

        try {
            return process.exitValue();
        } catch (final Throwable throwable) {
            process.destroyForcibly();
        }

        try {
            return process.exitValue();
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
                        AbsoluteThread.sleep(20);
                    }
                }
            }
        }

        return process.isAlive() ? -1 : process.exitValue();
    }
}
