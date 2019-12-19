package systems.reformcloud.reformcloud2.executor.api.common.network.auth;

import com.google.gson.reflect.TypeToken;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.auth.defaults.DefaultAuth;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

public interface Auth extends Nameable {

  TypeToken<DefaultAuth> TYPE = new TypeToken<DefaultAuth>() {};

  /**
   * @return The authentication key of the current auth
   */
  @Nonnull String key();

  /**
   * @return The parent component of the network instance
   */
  @Nullable String parent();

  /**
   * @return The current network type of the cloud
   */
  @Nonnull NetworkType type();

  /**
   * @return Extra data provided per component
   */
  @Nonnull JsonConfiguration extra();
}
