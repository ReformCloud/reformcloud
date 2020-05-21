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
package systems.reformcloud.reformcloud2.signs.util;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.signs.application.packets.PacketRequestSignLayouts;
import systems.reformcloud.reformcloud2.signs.application.packets.PacketRequestSignLayoutsResult;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignConfig;

import java.util.function.Consumer;

public final class ConfigRequesterUtil {

    private ConfigRequesterUtil() {
        throw new UnsupportedOperationException();
    }

    public static void requestSignConfigAsync(Consumer<SignConfig> callback) {
        ExecutorAPI.getInstance().getPacketHandler().registerHandler(PacketRequestSignLayoutsResult.class);
        Task.EXECUTOR.execute(() -> {
            while (!DefaultChannelManager.INSTANCE.get("Controller").isPresent()) {
                AbsoluteThread.sleep(200);
            }

            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(e -> ExecutorAPI.getInstance().getPacketHandler().getQueryHandler().sendQueryAsync(e, new PacketRequestSignLayouts()).onComplete(r -> {
                if (r instanceof PacketRequestSignLayoutsResult) {
                    callback.accept(((PacketRequestSignLayoutsResult) r).getSignConfig());
                }
            }));
        });
    }
}
