package systems.reformcloud.reformcloud2.executor.api.common.client;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.client.basic.DefaultClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import java.util.UUID;

/**
 * This class represents any connected to a controller
 */
public interface ClientRuntimeInformation extends Nameable {

    TypeToken<DefaultClientRuntimeInformation> TYPE = new TypeToken<DefaultClientRuntimeInformation>() {
    };

    /**
     * @return The start host of the client
     */
    @NotNull
    String startHost();

    /**
     * @return The unique id of the client
     */
    @NotNull
    UUID uniqueID();

    /**
     * @return The max memory of the client
     */
    int maxMemory();

    /**
     * @return The max process count of the client
     */
    int maxProcessCount();
}
