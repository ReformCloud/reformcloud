package systems.reformcloud.reformcloud2.executor.api.common.network.files.io.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.files.io.FileReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ChunkedFileReader implements FileReader {

    public ChunkedFileReader(@NotNull Path path) {
        try {
            this.inputStream = Files.newInputStream(path);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    public ChunkedFileReader(@NotNull InputStream inputStream) {
        this.inputStream = inputStream;
    }

    private InputStream inputStream;

    private int readNextChunk(byte[] target) {
        try {
            return inputStream.read(target);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        return -1;
    }

    @NotNull
    public byte[] newByteArray() {
        return new byte[256000];
    }

    @Override
    public int read(@NotNull byte[] bytes) {
        return readNextChunk(bytes);
    }
}
