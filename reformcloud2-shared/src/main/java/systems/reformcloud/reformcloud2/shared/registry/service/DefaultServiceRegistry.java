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
package systems.reformcloud.reformcloud2.shared.registry.service;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.registry.service.ServiceRegistry;
import systems.reformcloud.reformcloud2.executor.api.registry.service.ServiceRegistryEntry;
import systems.reformcloud.reformcloud2.executor.api.registry.service.exception.ProviderImmutableException;
import systems.reformcloud.reformcloud2.executor.api.registry.service.exception.ProviderNeedsReplacementException;
import systems.reformcloud.reformcloud2.executor.api.registry.service.exception.ProviderNotRegisteredException;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultServiceRegistry implements ServiceRegistry {

    private final Map<Class<?>, ServiceRegistryEntry<?>> entries = new ConcurrentHashMap<>();

    @Override
    public <T> void setProvider(@NotNull Class<T> service, @NotNull T provider, boolean immutable, boolean needsReplacement) throws ProviderImmutableException {
        ServiceRegistryEntry<?> current = this.entries.get(service);
        if (current != null && current.isImmutable()) {
            throw new ProviderImmutableException(service);
        }

        this.entries.put(service, new DefaultServiceRegistryEntry<>(service, provider, immutable, needsReplacement));
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getProvider(@NotNull Class<T> service) {
        ServiceRegistryEntry<T> entry = (ServiceRegistryEntry<T>) this.entries.get(service);
        return entry == null ? Optional.empty() : Optional.of(entry.getProvider());
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<ServiceRegistryEntry<T>> getRegisteredEntry(@NotNull Class<T> service) {
        ServiceRegistryEntry<T> entry = (ServiceRegistryEntry<T>) this.entries.get(service);
        return Optional.ofNullable(entry);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProviderUnchecked(@NotNull Class<T> service) throws ProviderNotRegisteredException {
        ServiceRegistryEntry<T> entry = (ServiceRegistryEntry<T>) this.entries.get(service);
        if (entry == null) {
            throw new ProviderNotRegisteredException(service);
        }

        return entry.getProvider();
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<ServiceRegistryEntry<?>> getRegisteredServices() {
        return Collections.unmodifiableCollection(this.entries.values());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void unregisterService(@NotNull Class<T> service, @Nullable T replacement) throws ProviderNotRegisteredException, ProviderImmutableException, ProviderNeedsReplacementException {
        ServiceRegistryEntry<T> entry = (ServiceRegistryEntry<T>) this.entries.get(service);
        if (entry == null) {
            throw new ProviderNotRegisteredException(service);
        }

        if (entry.isImmutable()) {
            throw new ProviderImmutableException(service);
        }

        if (entry.needsReplacement() && replacement == null) {
            throw new ProviderNeedsReplacementException(service);
        }

        this.entries.remove(service);
    }
}
