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
package systems.reformcloud.reformcloud2.executor.api.dependency;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * Represents a dependency which can be loaded dynamically in the runtime.
 *
 * @author derklaro
 * @since 7. October 2020
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dependency {

    /**
     * The group id of this dependency artifact
     *
     * @return the group id of this dependency artifact
     */
    @NotNull
    String groupId();

    /**
     * The artifact id of this dependency
     *
     * @return the artifact id of this dependency
     */
    @NotNull
    String artifactId();

    /**
     * The version of this dependency artifact
     *
     * @return the version of this dependency artifact
     */
    @NotNull
    String version();

    /**
     * Indicates that this dependency is optional and there is no need to load it. This means if we fail to download
     * this dependency the loading process will not be interrupted.
     *
     * @return if this dependency is optional
     */
    boolean optional() default false;

    /**
     * Defines the type of this dependency, this defaults to {@code jar}. This defines the file extension of this file.
     * Some examples are {@code jar} or {@code war}. This means the dependency artifact is loaded
     * using {@link #artifactId()}.{@code jar}.
     *
     * @return The file extension type of this dependency.
     */
    @NotNull
    String type() default "jar";

    /**
     * Represents the system path where the dependency is copied to in runtime. This path does not allow any {@code ../} and
     * {@code ..\} operations. If the path is empty (default case) the system path will be defined automatically.
     *
     * @return the system path this dependency will be located in
     */
    @NotNull
    String systemPath() default "";

    /**
     * The repository in which this dependency is located.
     *
     * @return the repository in which this dependency is located
     */
    @NotNull
    Repository repository();
}
