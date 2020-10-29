package systems.reformcloud.reformcloud2.executor.api.network.data;

import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import systems.reformcloud.reformcloud2.executor.api.network.SerializableObject;

import java.util.Arrays;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProtocolBufferTest {

    private final ProtocolBuffer buffer = new DefaultProtocolBuffer(Unpooled.buffer());

    @Test
    @Order(1)
    void testBufferWrite() {
        this.buffer.writeString("test");
        this.buffer.writeArray(new byte[]{1, 2});
        this.buffer.writeVarInt(15);
        this.buffer.writeUniqueId(UUID.fromString("bcc582ed-494d-4b93-86cb-b58564651a26"));
        this.buffer.writeObject(new TestObject());

        this.buffer.readerIndex(0);
    }

    @Test
    @Order(2)
    void testBufferRead() {
        Assertions.assertEquals("test", this.buffer.readString());
        Assertions.assertTrue(Arrays.equals(new byte[]{1, 2}, this.buffer.readArray()));
        Assertions.assertEquals(15, this.buffer.readVarInt());
        Assertions.assertEquals(UUID.fromString("bcc582ed-494d-4b93-86cb-b58564651a26"), this.buffer.readUniqueId());

        TestObject object = this.buffer.readObject(TestObject.class);
        Assertions.assertNotNull(object);
        Assertions.assertEquals("test", object.testString);
    }

    @AfterAll
    void release() {
        this.buffer.release();
    }

    public static class TestObject implements SerializableObject {

        public String testString;

        @Override
        public void write(@NotNull ProtocolBuffer buffer) {
            buffer.writeString("test");
        }

        @Override
        public void read(@NotNull ProtocolBuffer buffer) {
            this.testString = buffer.readString();
        }
    }
}
