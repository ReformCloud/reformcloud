package systems.reformcloud.reformcloud2.executor.api.common.network.files.io;

import javax.annotation.Nonnull;

public interface FileReader {

    int read(@Nonnull byte[] bytes);

    @Nonnull
    byte[] newByteArray();

}
