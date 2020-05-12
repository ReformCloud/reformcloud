/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
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
package systems.reformcloud.reformcloud2.permissions.sponge;

import com.google.inject.Inject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.permission.PermissionService;
import systems.reformcloud.reformcloud2.permissions.sponge.listener.SpongePermissionListener;
import systems.reformcloud.reformcloud2.permissions.sponge.service.SpongePermissionService;
import systems.reformcloud.reformcloud2.permissions.util.PermissionPluginUtil;

@Plugin(
        id = "reformcloud_2_perms",
        name = "SpongePermissionPlugin",
        version = "2",
        description = "The reformcloud permission plugin",
        authors = {"derklaro"},
        url = "https://reformcloud.systems",
        dependencies = {@Dependency(id = "reformcloud_2_api_executor")}
)
public class SpongePermissionPlugin {

    @Inject
    private ServiceManager serviceManager;

    @Listener
    public void handle(final GameStartedServerEvent event) {
        PermissionPluginUtil.awaitConnection(() -> {
            this.serviceManager.setProvider(this, PermissionService.class, new SpongePermissionService());
            Sponge.getEventManager().registerListeners(this, new SpongePermissionListener());
        });
    }
}
