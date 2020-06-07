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
package systems.refomcloud.reformcloud2.embedded.plugin.spigot;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import systems.refomcloud.reformcloud2.embedded.plugin.spigot.event.ExtraListenerHandler;
import systems.refomcloud.reformcloud2.embedded.plugin.spigot.event.PlayerListenerHandler;
import systems.reformcloud.reformcloud2.executor.api.language.loading.LanguageLoader;

public final class SpigotLauncher extends JavaPlugin {

    @Override
    public void onLoad() {
        LanguageLoader.doLoad();
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    @Override
    public void onEnable() {
        new SpigotExecutor(this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListenerHandler(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ExtraListenerHandler(), this);
    }

    @Override
    public void onDisable() {
        SpigotExecutor.getInstance().getNetworkClient().disconnect();
        Bukkit.getScheduler().cancelTasks(this);

        Bukkit.getOnlinePlayers().forEach(e -> e.kickPlayer(""));
    }
}
