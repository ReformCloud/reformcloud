package systems.reformcloud.reformcloud2.executor.api.common.network.files.io.basic;

import systems.reformcloud.reformcloud2.executor.api.common.network.files.io.FileReader;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ChunkedFileReader implements FileReader {

    public ChunkedFileReader(@Nonnull Path path) {
        try {
            this.inputStream = Files.newInputStream(path);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    public ChunkedFileReader(@Nonnull InputStream inputStream) {
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

    @Nonnull
    public byte[] newByteArray() {
        return new byte[256000];
    }

    @Override
    public int read(@Nonnull byte[] bytes) {
        return readNextChunk(bytes);
    }
}
