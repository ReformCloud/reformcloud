package de.klaro.reformcloud2.executor.api.common.utility.update;

import de.klaro.reformcloud2.executor.api.common.utility.task.Task;

public interface Updateable<T> {

    /**
     * Updates a specific value
     * <p>
     * @param t The current object which should be updated
     */
    void update(T t);

    /**
     * Updates a specific value asynchronously
     *
     * @param t The current object which should be updated
     * @see #update(Object)
     */
    default void updateAsync(T t) {
        Task.EXECUTOR.execute(() -> update(t));
    }
}
