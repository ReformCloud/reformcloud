package systems.reformcloud.reformcloud2.executor.api.common.utility.update;

import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import javax.annotation.Nonnull;

public interface Updateable<T> {

    /**
     * Updates a specific value
     * <p>
     * @param t The current object which should be updated
     */
    void update(@Nonnull T t);

    /**
     * Updates a specific value asynchronously
     *
     * @param t The current object which should be updated
     * @see #update(Object)
     */
    default void updateAsync(@Nonnull T t) {
        Task.EXECUTOR.execute(() -> update(t));
    }
}
