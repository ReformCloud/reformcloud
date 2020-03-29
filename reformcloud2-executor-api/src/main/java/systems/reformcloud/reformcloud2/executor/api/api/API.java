package systems.reformcloud.reformcloud2.executor.api.api;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

/**
 * This class can only get called if the environment is {@link systems.reformcloud.reformcloud2.executor.api.ExecutorType#API}.
 * Check this by using {@link ExecutorAPI#getType()}. If the current instance is not an api instance
 * just use the default cloud api based on {@link ExecutorAPI#getInstance()}.
 */
public abstract class API extends ExternalAPIImplementation {

    /**
     * @return The current process information the current api instance is using
     */
    @NotNull
    public abstract ProcessInformation getCurrentProcessInformation();

    /**
     * @return The current api instance the cloud is running on
     */
    @NotNull
    public static API getInstance() {
        return (API) ExecutorAPI.getInstance();
    }
}
