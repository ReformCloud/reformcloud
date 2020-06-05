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
package systems.reformcloud.reformcloud2.protocol.shared;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.network.channel.EndpointChannelReader;
import systems.reformcloud.reformcloud2.executor.api.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.protocol.ProtocolPacket;

public class PacketAuthBegin extends ProtocolPacket {

    public PacketAuthBegin() {
    }

    public PacketAuthBegin(String connectionKey, int type, JsonConfiguration data) {
        this.connectionKey = connectionKey;
        this.type = type;
        this.data = data;
    }

    private String connectionKey;
    private int type;
    private JsonConfiguration data;

    public String getConnectionKey() {
        return this.connectionKey;
    }

    public int getType() {
        return this.type;
    }

    public JsonConfiguration getData() {
        return this.data;
    }

    @Override
    public int getId() {
        return NetworkUtil.AUTH_BUS;
    }

    @Override
    public void handlePacketReceive(@NotNull EndpointChannelReader reader, @NotNull NetworkChannel channel) {
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.connectionKey);
        buffer.writeInt(this.type);
        buffer.writeArray(this.data.toPrettyBytes());
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.connectionKey = buffer.readString();
        this.type = buffer.readInt();
        this.data = new JsonConfiguration(buffer.readArray());
    }
}
