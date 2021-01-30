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
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.registry.service.exception.ProviderImmutableException;
import systems.reformcloud.registry.service.exception.ProviderNeedsReplacementException;
import systems.reformcloud.registry.service.exception.ProviderNotRegisteredException;

import java.util.Collection;
import java.util.Optional;

/**
 * A service registry.
 */
public interface ServiceRegistry {

  /**
   * Gets a provider unchecked out of this service registry.
   *
   * @param service the class of the service to get.
   * @param <T>     the type of the service to get.
   * @return The registered service.
   * @throws ProviderNotRegisteredException When no provider is registered for.
   */
  @NotNull
  static <T> T getUnchecked(@NotNull Class<T> service) throws ProviderNotRegisteredException {
    return ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(service);
  }

  /**
   * Gets a provider out of this service registry.
   *
   * @param service the class of the service to get.
   * @param <T>     the type of the service to get.
   * @return The registered service.
   */
  @NotNull
  static <T> Optional<T> getProvided(@NotNull Class<T> service) {
    return ExecutorAPI.getInstance().getServiceRegistry().getProvider(service);
  }

  /**
   * Sets a provider in this registry.
   *
   * @param service  the class of the service to get.
   * @param provider the provider for the service.
   * @param <T>      the type of the service to get.
   * @throws ProviderImmutableException When an immutable provider is already registered in the registry.
   */
  default <T> void setProvider(@NotNull Class<T> service, @NotNull T provider) throws ProviderImmutableException {
    this.setProvider(service, provider, false);
  }

  /**
   * Sets a provider in this registry.
   *
   * @param service   the class of the service to get.
   * @param provider  the provider for the service.
   * @param immutable if the provider of the service is immutable.
   * @param <T>       the type of the service to get.
   * @throws ProviderImmutableException When an immutable provider is already registered in the registry.
   */
  default <T> void setProvider(@NotNull Class<T> service, @NotNull T provider, boolean immutable) throws ProviderImmutableException {
    this.setProvider(service, provider, immutable, false);
  }

  /**
   * Sets a provider in this registry.
   *
   * @param service          the class of the service to get.
   * @param provider         the provider for the service.
   * @param immutable        if the provider of the service is immutable.
   * @param needsReplacement if the provider of the service needs a replacement.
   * @param <T>              the type of the service to get.
   * @throws ProviderImmutableException When an immutable provider is already registered in the registry.
   */
  <T> void setProvider(@NotNull Class<T> service, @NotNull T provider, boolean immutable, boolean needsReplacement) throws ProviderImmutableException;

  /**
   * Gets a provider.
   *
   * @param service the class of the service to get.
   * @param <T>     the type of the service to get.
   * @return The registered service.
   */
  @NotNull <T> Optional<T> getProvider(@NotNull Class<T> service);

  /**
   * Gets an entry.
   *
   * @param service the class of the service to get.
   * @param <T>     the type of the service to get.
   * @return The registered entry.
   */
  @NotNull <T> Optional<ServiceRegistryEntry<T>> getRegisteredEntry(@NotNull Class<T> service);

  /**
   * Gets an entry unchecked.
   *
   * @param service the class of the service to get.
   * @param <T>     the type of the service to get.
   * @return The registered service.
   * @throws ProviderNotRegisteredException When no provider is registered for.
   */
  @NotNull <T> T getProviderUnchecked(@NotNull Class<T> service) throws ProviderNotRegisteredException;

  /**
   * Get all entries registered in this registry.
   *
   * @return All entries registered in this registry.
   */
  @NotNull
  @UnmodifiableView Collection<ServiceRegistryEntry<?>> getRegisteredServices();

  /**
   * Unregisters a service from this service registry.
   *
   * @param service the class of the service to get.
   * @param <T>     the type of the service to get.
   * @throws ProviderNotRegisteredException    When no provider is registered for.
   * @throws ProviderImmutableException        When an immutable provider is already registered in the registry.
   * @throws ProviderNeedsReplacementException When the provider needs an replacement.
   */
  default <T> void unregisterService(@NotNull Class<T> service) throws ProviderNotRegisteredException, ProviderImmutableException, ProviderNeedsReplacementException {
    this.unregisterService(service, null);
  }

  /**
   * Unregisters a service from this service registry.
   *
   * @param service     the class of the service to get.
   * @param replacement the replacement for the service.
   * @param <T>         the type of the service to get.
   * @throws ProviderNotRegisteredException    When no provider is registered for.
   * @throws ProviderImmutableException        When an immutable provider is already registered in the registry.
   * @throws ProviderNeedsReplacementException When the provider needs an replacement but {@code replacement} is null.
   */
  <T> void unregisterService(@NotNull Class<T> service, @Nullable T replacement) throws ProviderNotRegisteredException, ProviderImmutableException, ProviderNeedsReplacementException;

  /**
   * Checks if a service is registered in the service registry.
   *
   * @param service the class of the service to check.
   * @return If the service is registered in this service registry.
   */
  default boolean isRegistered(@NotNull Class<?> service) {
    return this.getProvider(service).isPresent();
  }
}
