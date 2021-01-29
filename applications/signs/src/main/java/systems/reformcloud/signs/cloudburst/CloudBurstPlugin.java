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
package systems.reformcloud.signs.cloudburst;

import com.google.inject.Inject;
import org.cloudburstmc.server.Server;
import org.cloudburstmc.server.event.Listener;
import org.cloudburstmc.server.event.server.ServerInitializationEvent;
import org.cloudburstmc.server.event.server.ServerShutdownEvent;
import org.cloudburstmc.server.event.server.ServerStartEvent;
import org.cloudburstmc.server.plugin.Dependency;
import org.cloudburstmc.server.plugin.Plugin;
import systems.reformcloud.signs.cloudburst.adapter.CloudBurstSignSystemAdapter;
import systems.reformcloud.signs.cloudburst.commands.CloudBurstCommandSigns;
import systems.reformcloud.signs.util.ConfigRequesterUtil;

@Plugin(
  id = "reformcloud_2_signs",
  name = "CloudBurstSingsPlugin",
  version = "2",
  description = "The reformcloud permission plugin",
  url = "https://reformcloud.systems",
  authors = {"derklaro"},
  dependencies = {@Dependency(id = "reformcloud_2_api_executor")}
)
public class CloudBurstPlugin {

  @Inject
  public CloudBurstPlugin() {
  }

  @Listener
  public void handle(ServerInitializationEvent event) {
    Server.getInstance().getCommandRegistry().register(
      Server.getInstance().getPluginManager().getPlugin("reformcloud_2_signs").orElse(null),
      new CloudBurstCommandSigns()
    );
  }

  @Listener
  public void handle(ServerStartEvent event) {
    ConfigRequesterUtil.requestSignConfigAsync(config -> new CloudBurstSignSystemAdapter(config, this));
  }

  @Listener
  public void handle(ServerShutdownEvent event) {
    Server.getInstance().getScheduler().cancelTask(this);
  }
}
