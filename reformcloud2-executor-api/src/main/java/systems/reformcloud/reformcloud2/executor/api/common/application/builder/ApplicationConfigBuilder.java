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
package systems.reformcloud.reformcloud2.executor.api.common.application.builder;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.application.ApplicationConfig;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.Dependency;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;

public final class ApplicationConfigBuilder {

    public ApplicationConfigBuilder(String name, String main, String author, String version, File appFile, JarEntry descFile) {
        this.name = name;
        this.main = main;
        this.author = author;
        this.version = version;
        this.appFile = appFile;
        this.descFile = descFile;
    }

    private final String name;

    private final String main;

    private final String author;

    private final String version;

    private final File appFile;

    private final JarEntry descFile;

    private String description = "A reformcloud application";

    private String website = "https://reformcloud.systems";

    private String implementedVersion = "2.2.0";

    private final List<Dependency> dependencies = new ArrayList<>();

    public ApplicationConfigBuilder withDependencies(List<Dependency> dependencies) {
        if (dependencies != null) {
            this.dependencies.addAll(dependencies);
        }

        return this;
    }

    public ApplicationConfigBuilder withDescription(String description) {
        if (description != null) {
            this.description = description;
        }

        return this;
    }

    public ApplicationConfigBuilder withWebsite(String website) {
        if (website != null) {
            this.website = website;
        }

        return this;
    }

    public ApplicationConfigBuilder withImplementedVersion(String impl) {
        if (impl != null) {
            this.implementedVersion = impl;
        }

        return this;
    }

    public ApplicationConfig create() {
        return new ApplicationConfig() {
            @Override
            @NotNull
            public String version() {
                return version;
            }

            @NotNull
            @Override
            public String author() {
                return author;
            }

            @NotNull
            @Override
            public String main() {
                return main;
            }

            @NotNull
            @Override
            public Dependency[] dependencies() {
                return dependencies.toArray(new Dependency[0]);
            }

            @NotNull
            @Override
            public String description() {
                return description;
            }

            @NotNull
            @Override
            public String website() {
                return website;
            }

            @NotNull
            @Override
            public String implementedVersion() {
                return implementedVersion;
            }

            @NotNull
            @Override
            public File applicationFile() {
                return appFile;
            }

            @NotNull
            @Override
            public JarEntry applicationConfigFile() {
                return descFile;
            }

            @NotNull
            @Override
            public String getName() {
                return name;
            }
        };
    }
}
