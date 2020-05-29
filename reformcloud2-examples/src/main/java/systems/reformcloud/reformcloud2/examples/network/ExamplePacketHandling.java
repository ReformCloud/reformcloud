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

import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.network.channel.manager.DefaultChannelManager;

/**
 * This class shows how you can handle the packet you have created
 */
public class ExamplePacketHandling {

    /* registers the packet for the handling in the network */
    public static void registerOwnPackets() {
        ExecutorAPI.getInstance().getPacketHandler().registerHandler(ExamplePacket.class);
    }

    /*
    Sends the created example packet to the network component named controller. By default, every
    controller or node listen on this name from the api or a client.
    In the network from node to node every node is registered by the real name of it.

    This methods sends a packet as an api component to the main network component where the registered handler
    will get called
     */
    public static void sendPacketAsApi() {
        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(sender -> sender.sendPacket(new ExamplePacket("test")));
    }

    /*
    This methods sends a packet as another node in the cluster to the node named Node-1
     */
    public static void sendPacketAsNode() {
        DefaultChannelManager.INSTANCE.get("Node-1").ifPresent(sender -> sender.sendPacket(new ExamplePacket("test")));
    }
}
