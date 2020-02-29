package systems.reformcloud.reformcloud2.executor.api.common.utility.name;

import javax.annotation.Nonnull;

/**
 * A special nameable which can get renamed
 */
public interface ReNameable extends Nameable {

    void setName(@Nonnull String newName);
}
