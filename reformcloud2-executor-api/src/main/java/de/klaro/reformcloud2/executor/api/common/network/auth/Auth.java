package de.klaro.reformcloud2.executor.api.common.network.auth;

import com.google.gson.reflect.TypeToken;
import de.klaro.reformcloud2.executor.api.common.network.auth.defaults.DefaultAuth;
import de.klaro.reformcloud2.executor.api.common.utility.name.Nameable;

public interface Auth extends Nameable {

    TypeToken<DefaultAuth> TYPE = new TypeToken<DefaultAuth>() {};

    String key();

    String parent();

    NetworkType type();
}
