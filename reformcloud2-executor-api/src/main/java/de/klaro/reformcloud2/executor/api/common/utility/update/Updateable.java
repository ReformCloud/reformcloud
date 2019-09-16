package de.klaro.reformcloud2.executor.api.common.utility.update;

import de.klaro.reformcloud2.executor.api.common.utility.task.Task;

public interface Updateable<T> {

    void update(T t);

    default void updateAsync(T t) {
        Task.EXECUTOR.execute(() -> update(t));
    }
}
