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
package systems.reformcloud.reformcloud2.executor.api.network.channel;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.utility.name.ReNameable;

import java.net.InetSocketAddress;
import java.util.UUID;

public interface NetworkChannel extends ReNameable {

    /**
     * Sends a packet into the channel
     *
     * @param packet The packet which should get sent
     */
    void sendPacket(@NotNull Object packet);

    /**
     * Sends many packets into the channel
     *
     * @param packets The packets which should be sent
     */
    void sendPackets(@NonNls Object... packets);

    void sendQueryResult(@Nullable UUID queryUniqueID, @NotNull Packet result);

    /**
     * @return The connection time of the network channel
     */
    long getConnectionTime();

    /**
     * @return The ip-address if the channel
     */
    @NotNull
    String getAddress();

    /**
     * @return The socket address of the channel
     */
    @NotNull
    InetSocketAddress getEthernetAddress();

    void setRemoteAddress(@NotNull InetSocketAddress address);

    /**
     * @return If the current network channel is connected
     */
    boolean isConnected();

    boolean isAuthenticated();

    void setAuthenticated(boolean authenticated);

    /**
     * @see #isConnected()
     * Closes the current network channel
     */
    void close();
}
