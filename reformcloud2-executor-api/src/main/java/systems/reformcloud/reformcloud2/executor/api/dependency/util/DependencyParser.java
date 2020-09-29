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
package systems.reformcloud.reformcloud2.executor.api.dependency.util;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.dependency.DefaultDependency;
import systems.reformcloud.reformcloud2.executor.api.dependency.Dependency;
import systems.reformcloud.reformcloud2.executor.api.dependency.repo.DefaultRepositories;
import systems.reformcloud.reformcloud2.executor.api.dependency.repo.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public final class DependencyParser {

    private DependencyParser() {
        throw new UnsupportedOperationException();
    }

    public static Collection<Dependency> getAllDependencies(@NotNull String internalFilePath,
                                                            @NotNull Map<String, Repository> repositoryGetter,
                                                            @NotNull ClassLoader source) {
        Collection<Dependency> out = new ArrayList<>();
        for (String dependencyString : getDependenciesFromFile(internalFilePath, source)) {
            String[] split = dependencyString.split(":");
            if (split.length != 3) {
                continue;
            }

            out.add(new DefaultDependency(
                    repositoryGetter.getOrDefault(split[0] + ":" + split[1], DefaultRepositories.MAVEN_CENTRAL),
                    split[0], split[1], split[2])
            );
        }

        return out;
    }

    private static Collection<String> getDependenciesFromFile(String internalFilePath, ClassLoader source) {
        Collection<String> out = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(source.getResourceAsStream(internalFilePath))))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                out.add(line);
            }
        } catch (final IOException ex) {
            throw new RuntimeException("Unable to load internal dependencies file", ex);
        }

        return out;
    }
}
