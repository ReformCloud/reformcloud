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
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.process.ProcessInformation;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * A cache for process information, only available in the embedded environment.
 */
public interface ProcessCache {

  /**
   * Provides the associated process information with the given {@code name}, either from the cache
   * or requesting from the node.
   *
   * @param name The name of the process to get.
   * @return the associated process information.
   */
  @NotNull Optional<ProcessInformation> provide(@NotNull String name);

  /**
   * Provides the associated process information with the given {@code uniqueId}, either from the cache
   * or requesting from the node.
   *
   * @param uniqueId The unique id of the process to get.
   * @return the associated process information.
   */
  @NotNull Optional<ProcessInformation> provide(@NotNull UUID uniqueId);

  /**
   * Get all cached processes.
   *
   * @return all cached processes.
   */
  @NotNull @UnmodifiableView Set<ProcessInformation> getCache();

  /**
   * Get the current size of the cache.
   *
   * @return the current size of the cache.
   */
  int getSize();

  /**
   * Invalidates all entries from this cache.
   */
  void invalidate();

  /**
   * Invalidates all entries of this cache and re-requesting all known entries of the node.
   */
  void refresh();

  /**
   * Updates the cache policy, deciding which processes should get cached.
   *
   * @param policy The new cache policy to use.
   */
  void setCachePolicy(@NotNull ProcessCachePolicy policy);
}
