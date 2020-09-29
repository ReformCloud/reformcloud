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
package systems.reformcloud.reformcloud2.proxy.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.event.EventManager;
import systems.reformcloud.reformcloud2.proxy.plugin.PluginConfigHandler;
import systems.reformcloud.reformcloud2.proxy.velocity.listener.VelocityListener;
import systems.reformcloud.reformcloud2.proxy.velocity.listener.VelocityProxyConfigurationHandlerSetupListener;

@Plugin(
        id = "reformcloud_2_proxy",
        name = "ReformCloud2Proxy",
        version = "2.0",
        description = "The proxy plugin",
        url = "https://reformcloud.systems",
        authors = {"derklaro"},
        dependencies = {@Dependency(id = "reformcloud_2_api_executor")}
)
public class VelocityPlugin {

    private final ProxyServer server;

    @Inject
    public VelocityPlugin(ProxyServer server) {
        this.server = server;
    }

    @Subscribe
    public void handle(final ProxyInitializeEvent event) {
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).registerListener(new VelocityProxyConfigurationHandlerSetupListener(this.server));

        PluginConfigHandler.request(() -> {
            VelocityListener listener = new VelocityListener(this.server);
            this.server.getEventManager().register(this, listener);
            ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).registerListener(listener);
        });
    }
}
