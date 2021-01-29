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
