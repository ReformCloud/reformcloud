package systems.reformcloud.reformcloud2.executor.api.common.client;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.client.basic.DefaultClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import javax.annotation.Nonnull;

/**
 * This class represents any connected to a controller
 */
public interface ClientRuntimeInformation extends Nameable {

    TypeToken<DefaultClientRuntimeInformation> TYPE = new TypeToken<DefaultClientRuntimeInformation>() {
    };

    /**
     * @return The start host of the client
     */
    @Nonnull
    String startHost();

    /**
     * @return The max memory of the client
     */
    int maxMemory();

    /**
     * @return The max process count of the client
     */
    int maxProcessCount();
}
