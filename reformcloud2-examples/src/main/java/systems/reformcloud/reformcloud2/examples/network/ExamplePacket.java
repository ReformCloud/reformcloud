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
package systems.reformcloud.reformcloud2.examples.network;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.network.channel.EndpointChannelReader;
import systems.reformcloud.reformcloud2.executor.api.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.network.netty.NettyChannelEndpoint;
import systems.reformcloud.reformcloud2.executor.api.network.packet.Packet;

/**
 * For this class we need the dependency executor-api and netty-codec
 */
public class ExamplePacket extends Packet {

    public ExamplePacket() {
        // The no args constructor is required in every packet for the decode process
    }

    public ExamplePacket(String exampleMessage) {
        this.exampleMessage = exampleMessage;
    }

    private String exampleMessage;

    private ExampleSerializableObject exampleSerializableObject;

    @Override
    public int getId() {
        // The id of a packet has to be unique!
        return 100;
    }

    @Override
    public void handlePacketReceive(@NotNull EndpointChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull NettyChannelEndpoint parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        System.out.println(this.exampleMessage); // Print out the read message from the read() method
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.exampleMessage); // write the example string into the buffer
        buffer.writeObject(this.exampleSerializableObject); // write the example serializable object into the buffer
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        // Read the objects in the same order as written into the buffer by the write() method
        this.exampleMessage = buffer.readString(); // Read the example string from the buffer
        this.exampleSerializableObject = buffer.readObject(ExampleSerializableObject.class); // read the serializable object by the class of the object
    }
}
