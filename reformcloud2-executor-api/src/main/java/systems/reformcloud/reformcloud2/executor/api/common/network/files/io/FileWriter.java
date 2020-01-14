package systems.reformcloud.reformcloud2.executor.api.common.network.files.io;

import javax.annotation.Nonnull;

public interface FileWriter {

    void write(@Nonnull byte[] bytes);

    void finish();
}
