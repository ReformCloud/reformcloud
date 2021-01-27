package systems.reformcloud.reformcloud2.shared.network.packet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import systems.reformcloud.reformcloud2.executor.api.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.network.packet.query.QueryManager;

import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultQueryManagerTest {

  private final QueryManager queryManager = new DefaultQueryManager();

  @Test
  public void testSendPacketQuery() {
    NetworkChannel dummyChannel = Mockito.mock(NetworkChannel.class);
    Packet dummyPacket = Mockito.mock(Packet.class);
    UUID queryUniqueId = UUID.randomUUID();

    this.queryManager.sendPacketQuery(dummyChannel, queryUniqueId, dummyPacket);
    Assertions.assertTrue(this.queryManager.getWaitingQuery(queryUniqueId).isPresent());
  }
}
