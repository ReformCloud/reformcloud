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
import systems.reformcloud.reformcloud2.executor.api.dependency.util.DependencyParser;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class DefaultDependencyLoader extends DependencyLoader {

    private static final String LOADING = "Preloading dependency %s %s from repo %s...";

    private static final String PATH = System.getProperty("reformcloud.lib.path");

    private final List<URL> urls = new ArrayList<>();

    @Override
    public void loadDependencies() {
        DependencyParser.getAllDependencies("internal/dependencies.txt", new HashMap<>(), DefaultDependencyLoader.class.getClassLoader()).forEach(e -> {
            System.out.println(String.format(LOADING, e.getArtifactID(), e.getVersion(), e.getRepository().getName()));
            this.urls.add(this.loadDependency(e));
        });
    }

    @Override
    public void addDependencies() {
        this.urls.forEach(this::addDependency);
    }

    @Override
    public URL loadDependency(@NotNull Dependency dependency) {
        Path path;
        if (DefaultDependencyLoader.PATH != null) {
            path = Paths.get(DefaultDependencyLoader.PATH + "/" + dependency.getPath());
        } else {
            path = dependency.getPath();
        }

        try {
            dependency.prepareIfUpdate();
            if (!Files.exists(path)) {
                dependency.download();
            }

            return path.toUri().toURL();
        } catch (final MalformedURLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public void addDependency(@NotNull URL depend) {
        DefaultDependencyLoader.this.addURL(depend);
    }
}
