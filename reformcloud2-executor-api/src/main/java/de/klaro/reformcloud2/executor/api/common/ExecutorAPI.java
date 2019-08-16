package de.klaro.reformcloud2.executor.api.common;

import de.klaro.reformcloud2.executor.api.common.base.Conditions;

public abstract class ExecutorAPI {

    private static ExecutorAPI instance;

    public ExecutorAPI(ExecutorAPI instance) {
        Conditions.isTrue(ExecutorAPI.instance == null, "API instance already set");
        ExecutorAPI.instance = instance;
    }

    public static ExecutorAPI getInstance() {
        return instance;
    }
}
