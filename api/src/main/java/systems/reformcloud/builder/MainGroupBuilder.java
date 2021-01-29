/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
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
package systems.reformcloud.builder;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.group.main.MainGroup;
import systems.reformcloud.task.Task;

import java.util.Collection;

/**
 * A builder to construct main groups
 */
public interface MainGroupBuilder {

  /**
   * Sets the name of the main group to create
   *
   * @param name The name of the main group
   * @return The same instance of this class as used to call the method
   */
  @NotNull
  MainGroupBuilder name(@NotNull String name);

  /**
   * Adds the given group names of sub groups to the new main group
   *
   * @param subGroups The sub groups which should extend the main groups
   * @return The same instance of this class as used to call the method
   */
  @NotNull
  MainGroupBuilder subGroups(@NonNls String... subGroups);

  /**
   * Adds the given group names of sub groups to the new main group
   *
   * @param subGroups The sub groups which should extend the main groups
   * @return The same instance of this class as used to call the method
   */
  @NotNull
  MainGroupBuilder subGroups(@NotNull Collection<String> subGroups);

  /**
   * @return Creates the new main group and will result with null if the group is already present, the new main group object otherwise
   */
  @NotNull
  Task<MainGroup> create();
}
