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
package systems.reformcloud.registry.service;

import org.jetbrains.annotations.NotNull;

/**
 * An entry in a {@link ServiceRegistry}.
 *
 * @param <T> the type of the entry.
 */
public interface ServiceRegistryEntry<T> {

  /**
   * Gets the class of the services provided by this entry.
   *
   * @return The class of the services provided by this entry.
   */
  @NotNull
  Class<T> getService();

  /**
   * Gets the instance of the service provider.
   *
   * @return The instance of the service provider.
   */
  @NotNull
  T getProvider();

  /**
   * Gets if this service is immutable.
   *
   * @return If this service is immutable.
   */
  boolean isImmutable();

  /**
   * Gets if this service needs a replacement when changing in the associated service registry.
   *
   * @return If this service needs a replacement when changing in the associated service registry.
   */
  boolean needsReplacement();
}
