package systems.reformcloud.reformcloud2.executor.api.common.utility.name;

import org.jetbrains.annotations.NotNull;

/**
 * Represents every object which can have a special name
 *
 * @see ReNameable
 */
public interface Nameable {

    /**
     * @return The name of the current instance
     */
    @NotNull
    String getName();
}
