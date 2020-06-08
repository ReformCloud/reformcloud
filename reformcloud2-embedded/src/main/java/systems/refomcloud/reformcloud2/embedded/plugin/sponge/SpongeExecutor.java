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
package systems.refomcloud.reformcloud2.embedded.plugin.sponge;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.refomcloud.reformcloud2.embedded.executor.PlayerAPIExecutor;
import systems.refomcloud.reformcloud2.embedded.plugin.sponge.executor.SpongePlayerExecutor;
import systems.refomcloud.reformcloud2.embedded.shared.SharedInvalidPlayerFixer;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;

public class SpongeExecutor extends Embedded {

    private static SpongeExecutor instance;
    private final SpongeLauncher plugin;
    private final SpongeExecutorService executorService;

    SpongeExecutor(SpongeLauncher launcher) {
        super.type = ExecutorType.API;
        PlayerAPIExecutor.setInstance(new SpongePlayerExecutor());

        this.plugin = launcher;
        this.executorService = Sponge.getScheduler().createSyncExecutor(launcher);
        instance = this;

        this.fixInvalidPlayers();
    }

    @NotNull
    public static SpongeExecutor getInstance() {
        return instance;
    }

    @NotNull
    public SpongeLauncher getPlugin() {
        return this.plugin;
    }

    @NotNull
    public SpongeExecutorService getExecutorService() {
        return this.executorService;
    }

    private void fixInvalidPlayers() {
        SharedInvalidPlayerFixer.start(
                uuid -> Sponge.getServer().getPlayer(uuid).isPresent(),
                () -> Sponge.getServer().getOnlinePlayers().size()
        );
    }
}
