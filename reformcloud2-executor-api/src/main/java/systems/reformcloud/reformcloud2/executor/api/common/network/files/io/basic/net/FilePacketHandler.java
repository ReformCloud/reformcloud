package systems.reformcloud.reformcloud2.executor.api.common.network.files.io.basic.net;

import systems.reformcloud.reformcloud2.executor.api.common.network.files.ChunkedFilePacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.files.io.FileWriter;
import systems.reformcloud.reformcloud2.executor.api.common.network.files.io.basic.ChunkedFileWriter;

import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FilePacketHandler extends ChunkedFilePacketHandler {

    private static final Map<UUID, FileWriter> FILE_WRITER_MAP = new ConcurrentHashMap<>();

    @Override
    public void handleChunkPacketReceive(UUID operationID, String path, int length, byte[] bytes) {
        if (!FILE_WRITER_MAP.containsKey(operationID)) {
            FILE_WRITER_MAP.put(operationID, new ChunkedFileWriter(Paths.get(path)));
        }

        FileWriter fileWriter = FILE_WRITER_MAP.get(operationID);
        if (length == -1) {
            fileWriter.finish();
            return;
        }

        fileWriter.write(bytes);
    }
}
