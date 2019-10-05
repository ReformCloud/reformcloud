package systems.reformcloud.reformcloud2.executor.api.common.client;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.client.basic.DefaultClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

public interface ClientRuntimeInformation extends Nameable {

    TypeToken<DefaultClientRuntimeInformation> TYPE = new TypeToken<DefaultClientRuntimeInformation>() {};

    String startHost();

    int maxMemory();

    int maxProcessCount();
}
