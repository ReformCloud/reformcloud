package systems.reformcloud.reformcloud2.executor.api.common.utility.clone;

import javax.annotation.Nonnull;

public interface Clone<T> extends Cloneable {

    /**
     * Clones the current instance of a class
     *
     * @return The cloned object of the current object instance
     */
    @Nonnull
    T clone();
}
