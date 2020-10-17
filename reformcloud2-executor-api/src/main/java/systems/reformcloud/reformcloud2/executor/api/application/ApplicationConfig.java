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
package systems.reformcloud.reformcloud2.executor.api.application;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.utility.name.Nameable;

import java.io.File;
import java.util.jar.JarEntry;

/**
 * Represents a config of an application
 *
 * @see LoadedApplication#getApplicationConfig()
 */
public interface ApplicationConfig extends Nameable {

    /**
     * @return The version of the application
     */
    @NotNull
    String getVersion();

    /**
     * @return The author of the application
     */
    @NotNull
    String getAuthor();

    /**
     * @return The main class of the application
     */
    @NotNull
    String getMainClassName();

    /**
     * @return The description of the application
     */
    @NotNull
    String getDescription();

    /**
     * @return The website of an application
     */
    @NotNull
    String getWebsite();

    /**
     * @return The api version which the application is using
     */
    @NotNull
    String getImplementedVersion();

    /**
     * @return The file from which the application is loaded
     */
    @NotNull
    File getApplicationFile();

    /**
     * @return The {@link JarEntry} from which the application config is loaded
     */
    @NotNull
    JarEntry getApplicationConfigJarEntry();
}
