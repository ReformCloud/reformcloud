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
package systems.reformcloud.reformcloud2.executor.api.network.channel.shared;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.network.channel.EndpointChannelReader;
import systems.reformcloud.reformcloud2.executor.api.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.network.packet.query.QueryManager;
import systems.reformcloud.reformcloud2.executor.api.task.Task;

import java.util.Optional;

public abstract class SharedEndpointChannelReader implements EndpointChannelReader {

    protected NetworkChannel networkChannel;

    @NotNull
    @Override
    public NetworkChannel getNetworkChannel() {
        return this.networkChannel;
    }

    @NotNull
    @Override
    public EndpointChannelReader setNetworkChannel(@NotNull NetworkChannel channel) {
        this.networkChannel = channel;
        return this;
    }

    @Override
    public void read(@NotNull Packet input) {
        NetworkUtil.EXECUTOR.execute(() -> {
            if (input.getQueryUniqueID() != null) {
                Optional<Task<Packet>> waitingQuery = ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(QueryManager.class).getWaitingQuery(input.getQueryUniqueID());
                if (waitingQuery.isPresent()) {
                    waitingQuery.get().complete(input);
                    return;
                }
            }

            try {
                input.handlePacketReceive(this, this.networkChannel);
            } catch (final Throwable throwable) {
                System.err.println("Error while handling packet " + input.getId() + "@" + input.getClass().getName());
                throwable.printStackTrace();
            }
        });
    }
}
