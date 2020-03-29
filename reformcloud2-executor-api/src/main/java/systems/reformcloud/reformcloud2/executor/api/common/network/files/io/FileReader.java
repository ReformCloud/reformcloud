package systems.reformcloud.reformcloud2.executor.api.common.network.files.io;

import org.jetbrains.annotations.NotNull;

public interface FileReader {

    int read(@NotNull byte[] bytes);

    @NotNull
    byte[] newByteArray();

}
