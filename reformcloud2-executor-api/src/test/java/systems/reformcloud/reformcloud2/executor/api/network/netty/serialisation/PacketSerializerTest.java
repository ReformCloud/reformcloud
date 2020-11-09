package systems.reformcloud.reformcloud2.executor.api.network.netty.serialisation;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.network.channel.listener.ChannelListener;
import systems.reformcloud.reformcloud2.executor.api.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.network.packet.PacketProvider;
import systems.reformcloud.reformcloud2.executor.api.registry.service.ServiceRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class PacketSerializerTest {

    @BeforeAll
    static void init() {
        PacketProvider dummyProvider = Mockito.mock(PacketProvider.class);
        Mockito.when(dummyProvider.getPacketById(0)).thenReturn(Optional.of(new TestPacket()));

        ServiceRegistry dummyRegistry = Mockito.mock(ServiceRegistry.class);
        Mockito.when(dummyRegistry.getProviderUnchecked(PacketProvider.class)).thenReturn(dummyProvider);

        ExecutorAPI dummyApi = Mockito.mock(ExecutorAPI.class);
        Mockito.when(dummyApi.getServiceRegistry()).thenReturn(dummyRegistry);

        ExecutorAPI.setInstance(dummyApi);
    }

    @Test
    void testPacketSerialization() {
        Channel channel = Mockito.mock(Channel.class);
        Mockito.when(channel.isActive()).thenReturn(true);

        ChannelHandlerContext context = Mockito.mock(ChannelHandlerContext.class);
        Mockito.when(context.channel()).thenReturn(channel);

        PacketSerializerEncoder encoder = new PacketSerializerEncoder();
        SerializedPacketDecoder decoder = new SerializedPacketDecoder();

        ByteBuf byteBuf = Unpooled.buffer();
        List<Object> results = new ArrayList<>();

        encoder.encode(context, new TestPacket(), byteBuf);
        decoder.decode(context, byteBuf, results);

        Assertions.assertEquals(1, results.size());
        Assertions.assertTrue(results.get(0) instanceof Packet);

        Packet packet = (Packet) results.get(0);
        Assertions.assertEquals(0, packet.getId());
        Assertions.assertNull(packet.getQueryUniqueID());
    }

    public static class TestPacket extends Packet {

        @Override
        public int getId() {
            return 0;
        }

        @Override
        public void handlePacketReceive(@NotNull ChannelListener reader, @NotNull NetworkChannel channel) {
        }

        @Override
        public void write(@NotNull ProtocolBuffer buffer) {
            buffer.writeString("lol");
        }

        @Override
        public void read(@NotNull ProtocolBuffer buffer) {
            Assertions.assertEquals("lol", buffer.readString());
        }
    }
}
