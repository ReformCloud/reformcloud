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
import org.jetbrains.annotations.UnmodifiableView;

import java.net.URLClassLoader;
import java.util.Collection;

/**
 * A wrapper which can automatically detect and load dependencies in runtime.
 *
 * @author derklaro
 * @since 7. October 2020
 */
public interface DependencyLoader {

  /**
   * Loads all provided dependencies.
   *
   * @param dependencies The dependencies which should get loaded
   */
  void load(@NotNull Collection<Dependency> dependencies);

  /**
   * Detects all {@link Dependency} annotations of a class and loads them.
   *
   * @param clazz the class to detect the dependencies of
   */
  void detectAndLoad(@NotNull Class<?> clazz);

  /**
   * Detects all {@link Dependency} annotations of a class and loads them.
   *
   * @param clazz the class to detect the dependencies of
   * @see #detectAndLoad(Class)
   */
  void detectAndLoad(@NotNull Object clazz);

  /**
   * Detects all dependencies a class is annotated with.
   *
   * @param clazz the class to detect the dependencies of
   * @return a collection of all detected dependencies
   */
  @NotNull
  @UnmodifiableView
  Collection<Dependency> detectDependencies(@NotNull Class<?> clazz);

  /**
   * Get the context class loader to load the dependencies from.
   *
   * @return the context class loader used to load dependencies.
   */
  @NotNull
  URLClassLoader getContextClassLoader();
}
