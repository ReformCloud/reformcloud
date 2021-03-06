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
package systems.reformcloud.reformcloud2.runner.instrumentation;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.runner.util.JarFileDirectoryStreamFilter;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarFile;

public final class RunnerPremain {

    public static void premain(@NotNull String agentArgs, @NotNull Instrumentation instrumentation) {
        if (System.getProperty("reformcloud.lib.path") == null || System.getProperty("reformcloud.process.path") == null) {
            return;
        }

        Path path = Paths.get(System.getProperty("reformcloud.lib.path") + "/reformcloud/.bin/libs/");
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            throw new RuntimeException("Unable to parse runtime libs path");
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, new JarFileDirectoryStreamFilter())) {
            instrumentation.appendToSystemClassLoaderSearch(new JarFile(System.getProperty("reformcloud.process.path")));

            for (Path value : stream) {
                instrumentation.appendToSystemClassLoaderSearch(new JarFile(value.toFile()));
            }
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
