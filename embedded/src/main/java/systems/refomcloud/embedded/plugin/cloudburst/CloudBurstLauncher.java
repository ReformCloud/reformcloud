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
package systems.refomcloud.embedded.plugin.cloudburst;

import com.google.inject.Inject;
import org.cloudburstmc.server.Server;
import org.cloudburstmc.server.event.Listener;
import org.cloudburstmc.server.event.server.ServerShutdownEvent;
import org.cloudburstmc.server.event.server.ServerStartEvent;
import org.cloudburstmc.server.plugin.Plugin;
import systems.refomcloud.embedded.plugin.cloudburst.event.PlayerListenerHandler;

@Plugin(
  id = "reformcloud_api_executor",
  name = "ReformCloudCloudBurstExecutor",
  version = "3",
  description = "The reformcloud executor api",
  authors = {
    "derklaro",
    "ReformCloud-Team"
  },
  url = "https://reformcloud.systems"
)
public final class CloudBurstLauncher {

  @Inject
  public CloudBurstLauncher() {
  }

  @Listener
  public void handle(ServerStartEvent event) {
    new CloudBurstExecutor(this);
    Server.getInstance().getEventManager().registerListeners(this, new PlayerListenerHandler());
  }

  @Listener
  public void handle(ServerShutdownEvent event) {
    Server.getInstance().getScheduler().cancelTask(this);
  }
}
