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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * For internal use, see {@link ProcessCachePolicy}.
 */
@ApiStatus.Internal
final class ProcessCachePolicies {
  /**
   * Use {@link ProcessCachePolicy#always()}
   */
  public static final ProcessCachePolicy ALWAYS = information -> true;
  /**
   * Use {@link ProcessCachePolicy#never()}
   */
  public static final ProcessCachePolicy NEVER = information -> false;

  private ProcessCachePolicies() {
    throw new UnsupportedOperationException();
  }

  /**
   * Get a process cache policy which caches all process information which are
   * created using one group defined in {@code groups}.
   *
   * @param groups The names of the groups which should get cached.
   * @return a process cache policy which caches all information of specific groups.
   * @see ProcessCachePolicy#groups(String...)
   */
  @NotNull
  public static ProcessCachePolicy groups(@NotNull String... groups) {
    return processInformation -> Arrays.binarySearch(groups, processInformation.getProcessGroup().getName()) >= 0;
  }
}
