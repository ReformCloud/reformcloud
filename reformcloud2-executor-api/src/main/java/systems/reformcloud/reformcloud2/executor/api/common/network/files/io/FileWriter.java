package systems.reformcloud.reformcloud2.executor.api.common.network.files.io;

import org.jetbrains.annotations.NotNull;

public interface FileWriter {

    void write(@NotNull byte[] bytes);

    void finish();
}
