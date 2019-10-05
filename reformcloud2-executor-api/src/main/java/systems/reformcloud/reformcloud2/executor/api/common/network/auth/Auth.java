package systems.reformcloud.reformcloud2.executor.api.common.network.auth;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.auth.defaults.DefaultAuth;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

public interface Auth extends Nameable {

    TypeToken<DefaultAuth> TYPE = new TypeToken<DefaultAuth>() {};

    String key();

    String parent();

    NetworkType type();

    JsonConfiguration extra();
}
