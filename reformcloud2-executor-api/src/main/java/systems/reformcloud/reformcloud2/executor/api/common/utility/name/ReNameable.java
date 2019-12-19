package systems.reformcloud.reformcloud2.executor.api.common.utility.name;

import javax.annotation.Nonnull;

public interface ReNameable extends Nameable {

    void setName(@Nonnull String newName);
}
