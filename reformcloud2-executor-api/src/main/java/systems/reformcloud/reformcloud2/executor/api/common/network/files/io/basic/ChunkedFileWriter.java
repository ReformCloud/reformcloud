package systems.reformcloud.reformcloud2.executor.api.common.network.files.io.basic;

import systems.reformcloud.reformcloud2.executor.api.common.network.files.io.FileWriter;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ChunkedFileWriter implements FileWriter {

    public ChunkedFileWriter(@Nonnull Path target) {
        SystemHelper.deleteFile(target.toFile());
        SystemHelper.createFile(target);

        try {
            this.outputStream = Files.newOutputStream(target);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    public ChunkedFileWriter(@Nonnull OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    private OutputStream outputStream;

    @Override
    public void write(@Nonnull byte[] bytes) {
        try {
            outputStream.write(bytes);
            outputStream.flush();
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void finish() {
        try {
            outputStream.close();
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
}
