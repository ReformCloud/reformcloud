package systems.reformcloud.reformcloud2.executor.api.common.utility.function;

import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Duo;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface DoubleFunction<S, V, F> {

    /**
     * Get the result of the function
     *
     * @param s The known value which should get converted to a double
     * @return The created double of the given object
     */
    @Nonnull
    Duo<V, F> apply(@Nonnull S s);
}
