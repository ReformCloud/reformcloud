package systems.reformcloud.reformcloud2.shared.network.packet;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import systems.reformcloud.reformcloud2.executor.api.network.channel.EndpointChannelReader;
import systems.reformcloud.reformcloud2.executor.api.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.network.packet.PacketProvider;
import systems.reformcloud.reformcloud2.executor.api.network.packet.exception.PacketAlreadyRegisteredException;
import systems.reformcloud.reformcloud2.protocol.EmptyProtocolPacket;

import java.util.EmptyStackException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DefaultPacketProviderTest {

    private final PacketProvider packetProvider = new DefaultPacketProvider();

    @Test
    @Order(1)
    void testPacketRegister() {
        Packet packet = Mockito.mock(EmptyProtocolPacket.class);
        Mockito.when(packet.getId()).thenReturn(0);

        this.packetProvider.registerPacket(packet);
        Assertions.assertTrue(this.packetProvider.getPacketById(0).isPresent());
        Assertions.assertThrows(PacketAlreadyRegisteredException.class, () -> this.packetProvider.registerPacket(packet));
    }

    @Test
    @Order(2)
    void testPacketRegisterClazz() {
        Assertions.assertThrows(EmptyStackException.class, () -> this.packetProvider.registerPacket(TestPacketTwo.class));
        this.packetProvider.registerPacket(new TestPacketTwo(null));
        Assertions.assertThrows(EmptyStackException.class, () -> this.packetProvider.getPacketById(1));
    }

    @Test
    @Order(3)
    void testUnregisterPacket() {
        this.packetProvider.unregisterPacket(1);
        Assertions.assertFalse(this.packetProvider.getPacketById(1).isPresent());
    }

    @Test
    @Order(4)
    void testClearRegisteredPackets() {
        this.packetProvider.clearRegisteredPackets();
        Assertions.assertFalse(this.packetProvider.getPacketById(0).isPresent());
    }

    public static class TestPacketTwo extends EmptyProtocolPacket {

        public TestPacketTwo(Object ignored) {
        }

        @Override
        public int getId() {
            return 1;
        }

        @Override
        public void handlePacketReceive(@NotNull EndpointChannelReader reader, @NotNull NetworkChannel channel) {
        }
    }

}