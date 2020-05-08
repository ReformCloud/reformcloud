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
package systems.reformcloud.reformcloud2.executor.api.common.process.running.inclusions;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessInclusion;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.DownloadHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.HashMap;

public final class InclusionLoader {

    private InclusionLoader() {
        throw new UnsupportedOperationException();
    }

    public static void loadInclusions(@NotNull Path processPath, @NotNull Collection<ProcessInclusion> inclusions) {
        inclusions.forEach(inclusion -> {
            Path target = Paths.get("reformcloud/files/inclusions", inclusion.getName());
            if (Files.exists(target)) {
                return;
            }

            DownloadHelper.openConnection(
                    inclusion.getUrl(),
                    new HashMap<>(),
                    stream -> {
                        try {
                            SystemHelper.createDirectory(target.getParent());
                            Files.copy(stream, target);
                        } catch (final Throwable throwable) {
                            System.err.println("Unable to prepare inclusion " + inclusion.getName() + "!");
                            System.err.println("The error was: " + throwable.getMessage());
                        }
                    }, throwable -> {
                        System.err.println("Unable to open connection to given download server " + inclusion.getUrl());
                        System.err.println("The error was: " + throwable.getMessage());
                    }
            );
        });

        inclusions.forEach(inclusion -> {
            Path path = Paths.get("reformcloud/files/inclusions", inclusion.getName());
            if (Files.notExists(path)) {
                return;
            }

            Path target = Paths.get(processPath.toString(), inclusion.getName());
            try {
                SystemHelper.createDirectory(target.getParent());
                Files.copy(path, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}
