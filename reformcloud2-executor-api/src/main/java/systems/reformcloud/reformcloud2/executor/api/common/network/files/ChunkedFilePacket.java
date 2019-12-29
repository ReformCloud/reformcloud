package systems.reformcloud.reformcloud2.executor.api.common.network.files;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

import java.util.UUID;

public class ChunkedFilePacket extends JsonPacket {

    public ChunkedFilePacket(UUID uniqueID, String path, int readLength, byte[] current) {
        super(NetworkUtil.FILE_BUS + 1, new JsonConfiguration()
                .add("key", uniqueID)
                .add("path", path)
                .add("length", readLength), null, current);
    }
}
