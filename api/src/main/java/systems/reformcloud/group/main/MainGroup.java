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
package systems.reformcloud.group.main;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.builder.MainGroupBuilder;
import systems.reformcloud.configuration.data.JsonDataHolder;
import systems.reformcloud.network.data.SerializableObject;
import systems.reformcloud.utility.name.Nameable;

import java.util.Collection;
import java.util.Optional;

/**
 * A main group is an ordering util group which allows users to group their {@code ProcessGroups} to
 * manage them easier. A main group just holds a name and a list of sub groups. It can have additional
 * data stored in the object.
 */
public interface MainGroup extends Nameable, JsonDataHolder<MainGroup>, SerializableObject, Cloneable {

  /**
   * Creates a new builder for a main group.
   *
   * @param name The name of the main group to create.
   * @return A new builder for a main group.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  static MainGroupBuilder builder(@NotNull String name) {
    return ExecutorAPI.getInstance().getMainGroupProvider().createMainGroup(name);
  }

  /**
   * Get a main group by the {@code name} of it.
   *
   * @param name The name of the main group to get.
   * @return The main group.
   */
  @NotNull
  static Optional<MainGroup> getByName(@NotNull String name) {
    return ExecutorAPI.getInstance().getMainGroupProvider().getMainGroup(name);
  }

  /**
   * Get the sub groups of this main group.
   *
   * @return The sub groups of this main group.
   */
  @NotNull
  @UnmodifiableView
  Collection<String> getSubGroups();

  /**
   * Sets the sub groups of this main group.
   *
   * @param subGroups The new sub groups.
   */
  void setSubGroups(@NotNull Collection<String> subGroups);

  /**
   * Adds a sub group to this main group.
   *
   * @param subGroup The name of the {@code ProcessGroup} to add.
   */
  void addSubGroup(@NotNull String subGroup);

  /**
   * Removes a sub group from this main group.
   *
   * @param subGroup The name of the {@code ProcessGroup} to remove.
   */
  void removeSubGroup(@NotNull String subGroup);

  /**
   * Checks if the provided {@code name} is a name of a sub group managed by this main group.
   *
   * @param name The name of the {@code ProcessGroup} to check.
   * @return {@code true} if the {@code name} is a sub group of this main group, else {@code false}
   */
  boolean hasSubGroup(@NotNull String name);

  /**
   * Clears the sub groups of this main group.
   */
  void removeAllSubGroups();

  /**
   * Publishes an update of the main group. This operation is needed in order to
   * synchronize the changes.
   */
  void update();

  /**
   * Creates a clone of the main group.
   *
   * @return A clone of this main group.
   */
  @NotNull
  MainGroup clone();
}
