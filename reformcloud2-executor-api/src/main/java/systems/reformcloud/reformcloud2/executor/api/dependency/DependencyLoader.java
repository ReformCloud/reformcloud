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
package systems.reformcloud.reformcloud2.executor.api.dependency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.runner.RunnerClassLoader;

import java.net.URL;

public abstract class DependencyLoader {

    /**
     * Loads all default dependencies
     */
    public static void doLoad() {
        DependencyLoader dependencyLoader = new DefaultDependencyLoader();
        dependencyLoader.loadDependencies();
        dependencyLoader.addDependencies();
    }

    /**
     * Appends an url to the class loader search
     *
     * @param url The url which should be appended
     */
    void addURL(@NotNull URL url) {
        RunnerClassLoader urlClassLoader = (RunnerClassLoader) Thread.currentThread().getContextClassLoader();
        urlClassLoader.addURL(url);
    }

    /**
     * Loads all dependencies
     */
    public abstract void loadDependencies();

    /**
     * Adds all dependencies to the class loader search
     */
    public abstract void addDependencies();

    /**
     * Loads a specific dependency
     *
     * @param dependency The dependency which should be loaded
     * @return The file place of the dependency ad {@link URL}
     */
    @Nullable
    public abstract URL loadDependency(@NotNull Dependency dependency);

    /**
     * Adds the dependency location to the class loader search
     *
     * @param depend The {@link URL} to the place of the dependency
     */
    public abstract void addDependency(@NotNull URL depend);
}