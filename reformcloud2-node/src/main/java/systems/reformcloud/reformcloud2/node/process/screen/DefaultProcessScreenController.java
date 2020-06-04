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
package systems.reformcloud.reformcloud2.node.process.screen;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.node.NodeExecutor;
import systems.reformcloud.reformcloud2.node.process.DefaultNodeLocalProcessWrapper;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultProcessScreenController extends Thread implements ProcessScreenController {

    private final Map<UUID, ProcessScreen> processScreens = new ConcurrentHashMap<>();

    @Override
    public @NotNull ProcessScreen createScreen(@NotNull DefaultNodeLocalProcessWrapper wrapper) {
        ProcessScreen screen = new DefaultProcessScreen(wrapper);
        this.processScreens.put(wrapper.getProcessInformation().getProcessDetail().getProcessUniqueID(), screen);
        return screen;
    }

    @Override
    public @NotNull Optional<ProcessScreen> getScreen(@NotNull UUID processUniqueId) {
        return Optional.ofNullable(this.processScreens.get(processUniqueId));
    }

    @Override
    public void unregisterScreen(@NotNull UUID processUniqueId) {
        this.processScreens.remove(processUniqueId);
    }

    @Override
    public void tick() {
        this.processScreens.values().forEach(ProcessScreen::tick);
    }

    @Override
    public void run() {
        while (NodeExecutor.isRunning()) {
            this.tick();

            try {
                sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }
    }
}
