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
package systems.reformcloud.reformcloud2.shared.dependency;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.dependency.Dependencies;
import systems.reformcloud.reformcloud2.executor.api.dependency.Dependency;
import systems.reformcloud.reformcloud2.executor.api.dependency.DependencyLoader;
import systems.reformcloud.reformcloud2.shared.io.DownloadHelper;
import systems.reformcloud.reformcloud2.runner.RunnerClassLoader;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class DefaultDependencyLoader implements DependencyLoader {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.###");
    private static final String DEPENDENCY_DOWNLOAD_DONE_FORMAT = "Successfully completed download of dependency \"%s\" after %ss %n";
    private static final String DEPENDENCY_LOAD_FORMAT = "Loaded dependency %s:%s version %s %n";
    private static final String DEPENDENCY_DOWNLOAD_FORMAT = "Trying to download non-existing dependency \"%s\" version %s from %s (%s)... %n";

    private static final Path REPOSITORY_PATH = Paths.get(System.getProperty("reformcloud.lib.path", "reformcloud/.bin/libs"));

    private static Method addUrl;
    private final URLClassLoader contextLoader;

    public DefaultDependencyLoader() {
        Conditions.isTrue(Thread.currentThread().getContextClassLoader() instanceof URLClassLoader, "Thread context class loader is not an url class loader");
        this.contextLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private static Path getPath(@NotNull Dependency dependency) {
        String systemPath = dependency.systemPath();
        if (systemPath.trim().isEmpty()) {
            systemPath = dependency.groupId().replace(".", "/") + "/" + dependency.artifactId() + "/" + dependency.version() + "/";
        } else {
            systemPath = systemPath.replace("../", "").replace("..\\", "");
            if (!systemPath.endsWith(dependency.type()) && systemPath.endsWith("/")) {
                systemPath += "/";
            }
        }

        if (!systemPath.endsWith(dependency.type())) {
            systemPath += dependency.artifactId() + "-" + dependency.version() + "." + dependency.type();
        }

        return REPOSITORY_PATH.resolve(systemPath);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private static String getDownloadUrl(@NotNull Dependency dependency) {
        String repoUrl = dependency.repository().url().endsWith("/") ? dependency.repository().url() : dependency.repository().url() + "/";
        return repoUrl + dependency.groupId().replace(".", "/")
            + "/" + dependency.artifactId()
            + "/" + dependency.version()
            + "/" + dependency.artifactId() + "-" + dependency.version() + "." + dependency.type();
    }

    private static void handleException(@NotNull String format, @NotNull Dependency dependency, @NotNull Throwable exception) {
        RuntimeException runtimeException = new RuntimeException(String.format(format, dependency.toString()), exception);
        if (!dependency.optional()) {
            throw runtimeException;
        } else {
            runtimeException.printStackTrace();
        }
    }

    @Override
    public void load(@NotNull Collection<Dependency> dependencies) {
        // iterate over all dependencies and find the non-existing ones
        for (Dependency dependency : dependencies) {
            Path systemPath = getPath(dependency);
            if (Files.notExists(systemPath)) {
                System.out.printf(DEPENDENCY_DOWNLOAD_FORMAT, dependency.artifactId(), dependency.version(), dependency.repository().id(), dependency.repository().url());
                long start = System.currentTimeMillis();
                DownloadHelper.openConnection(
                    getDownloadUrl(dependency), Collections.emptyMap(), inputStream -> {
                        try {
                            Path parent = systemPath.getParent();
                            if (parent != null && Files.notExists(parent)) {
                                Files.createDirectories(parent);
                            }

                            Files.copy(inputStream, systemPath, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException exception) {
                            handleException("Unable to correctly download %s", dependency, exception);
                        }
                    }, throwable -> handleException("Unable to correctly download %s", dependency, throwable)
                );
                System.out.printf(DEPENDENCY_DOWNLOAD_DONE_FORMAT, dependency.artifactId(), DECIMAL_FORMAT.format((System.currentTimeMillis() - start) / 1000D));
            }
        }

        // We located all all dependencies and ensured they are downloaded if not already done. Now we can load and inject them to the context class loader
        for (Dependency dependency : dependencies) {
            Path systemPath = getPath(dependency);
            // Some dependencies may not exist locally because they are optional - skip them
            if (Files.exists(systemPath)) {
                try {
                    this.injectDependencyUrl(systemPath.toUri().toURL());
                    System.out.printf(DEPENDENCY_LOAD_FORMAT, dependency.groupId(), dependency.artifactId(), dependency.version());
                } catch (Exception exception) {
                    handleException("Unable to correctly load %s", dependency, exception);
                }
            }
        }
    }

    @Override
    public void detectAndLoad(@NotNull Class<?> clazz) {
        this.load(this.detectDependencies(clazz));
    }

    @Override
    public void detectAndLoad(@NotNull Object clazz) {
        this.detectAndLoad(clazz.getClass());
    }

    @Override
    public @NotNull @UnmodifiableView Collection<Dependency> detectDependencies(@NotNull Class<?> clazz) {
        for (Annotation annotation : clazz.getAnnotations()) {
            if (annotation instanceof Dependency) {
                return Collections.singletonList((Dependency) annotation);
            }

            if (annotation instanceof Dependencies) {
                return Arrays.asList(((Dependencies) annotation).value());
            }
        }

        return Collections.emptyList();
    }

    @Override
    public @NotNull URLClassLoader getContextClassLoader() {
        return this.contextLoader;
    }

    private void injectDependencyUrl(@NotNull URL url) throws Exception {
        if (this.contextLoader instanceof RunnerClassLoader) {
            ((RunnerClassLoader) this.contextLoader).addURL(url);
        } else {
            if (addUrl == null) {
                addUrl = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                addUrl.setAccessible(true);
            }

            addUrl.invoke(this.contextLoader, url);
        }
    }
}
