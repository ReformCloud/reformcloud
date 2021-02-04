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
package systems.reformcloud.shared.network.channel;

import io.netty.channel.Channel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import systems.reformcloud.network.channel.NetworkChannel;
import systems.reformcloud.network.channel.manager.ChannelManager;

import java.net.InetSocketAddress;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DefaultChannelManagerTest {

  private final ChannelManager channelManager = new DefaultChannelManager();

  @Test
  @Order(1)
  void testCreateAndRegisterChannel() {
    Channel channel = Mockito.mock(Channel.class);
    Mockito.when(channel.localAddress()).thenReturn(new InetSocketAddress("127.0.0.1", 25565));
    Mockito.when(channel.remoteAddress()).thenReturn(new InetSocketAddress("127.0.0.1", 25565));

    NetworkChannel networkChannel = new DefaultNetworkChannel(channel);
    networkChannel.setName("TestChannel");
    this.channelManager.registerChannel(networkChannel);
  }

  @Test
  @Order(2)
  void testGetFirstChannel() {
    Assertions.assertNotNull(this.channelManager.getFirstChannel());
  }

  @Test
  @Order(3)
  void testGetChannel() {
    Assertions.assertTrue(this.channelManager.getChannel("TestChannel").isPresent());
    Assertions.assertFalse(this.channelManager.getChannel("Testchannel").isPresent());
  }

  @Test
  @Order(4)
  void testGetNetworkChannels() {
    Assertions.assertEquals(1, this.channelManager.getNetworkChannels("127.0.0.1").size());
  }

  @Test
  @Order(5)
  void testUnregisterChannel() {
    this.channelManager.unregisterChannel("TestChannel");
    Assertions.assertEquals(0, this.channelManager.getRegisteredChannels().size());
  }
}
