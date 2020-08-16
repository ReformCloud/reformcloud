package systems.reformcloud.reformcloud2.protocol;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import systems.reformcloud.reformcloud2.protocol.processor.PacketProcessor;
import systems.reformcloud.reformcloud2.protocol.processor.PacketProcessorManager;
import systems.reformcloud.reformcloud2.protocol.shared.PacketAuthBegin;
import systems.reformcloud.reformcloud2.protocol.shared.PacketAuthSuccess;

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