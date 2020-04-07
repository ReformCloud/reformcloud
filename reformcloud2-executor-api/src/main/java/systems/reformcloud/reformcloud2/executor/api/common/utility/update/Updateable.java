package systems.reformcloud.reformcloud2.executor.api.common.utility.update;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

public interface Updateable<T> {

    /**
     * Updates a specific value
     * <p>
     * @param t The current object which should be updated
     */
    void update(@NotNull T t);

    /**
     * Updates a specific value asynchronously
     *
     * @param t The current object which should be updated
     * @see #update(Object)
     */
    default void updateAsync(@NotNull T t) {
        Task.EXECUTOR.execute(() -> update(t));
    }
}
