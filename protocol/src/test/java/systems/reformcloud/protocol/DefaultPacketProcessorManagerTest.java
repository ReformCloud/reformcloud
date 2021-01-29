package systems.reformcloud.protocol;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import systems.reformcloud.protocol.processor.PacketProcessor;
import systems.reformcloud.protocol.processor.PacketProcessorManager;
import systems.reformcloud.protocol.shared.PacketAuthBegin;
import systems.reformcloud.protocol.shared.PacketAuthSuccess;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DefaultPacketProcessorManagerTest {

  private final PacketProcessorManager processorManager = new DefaultPacketProcessorManager();

  @Test
  @Order(1)
  void testRegisterProcessor() {
    PacketProcessor<PacketAuthBegin> packetProcessor = Mockito.mock(PacketProcessor.class);
    this.processorManager.registerProcessor(packetProcessor, PacketAuthBegin.class);
  }

  @Test
  @Order(2)
  void testGetPacketProcessor() {
    Assertions.assertTrue(this.processorManager.getPacketProcessor(PacketAuthBegin.class).isPresent());
    Assertions.assertFalse(this.processorManager.getPacketProcessor(PacketAuthSuccess.class).isPresent());
  }
}
