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
package systems.refomcloud.embedded.cache;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.process.ProcessInformation;

/**
 * A policy for caching of services. Basically this is a predicate which
 * decides weather a process information should get cached or not.
 *
 * @since 3.0.0-SNAPSHOT
 */
@FunctionalInterface
public interface ProcessCachePolicy {
  /**
   * Get a process cache policy which caches all process information.
   *
   * @return a process cache policy which caches all process information.
   */
  static @NotNull ProcessCachePolicy always() {
    return ProcessCachePolicies.ALWAYS;
  }

  /**
   * Get a process cache policy which caches no process information.
   *
   * @return a process cache policy which caches no process information.
   */
  static @NotNull ProcessCachePolicy never() {
    return ProcessCachePolicies.NEVER;
  }

  /**
   * Get a process cache policy which caches all process information which are
   * created using one group defined in {@code groupName}.
   *
   * @param groupNames The names of the groups which should get cached.
   * @return a process cache policy which caches all information of specific groups.
   */
  static @NotNull ProcessCachePolicy groups(@NotNull String... groupNames) {
    return ProcessCachePolicies.groups(groupNames);
  }

  /**
   * Decides if a process information should get cached or not.
   *
   * @param information The information to check.
   * @return If the information should be cached.
   */
  boolean shouldCache(@NotNull ProcessInformation information);
}
