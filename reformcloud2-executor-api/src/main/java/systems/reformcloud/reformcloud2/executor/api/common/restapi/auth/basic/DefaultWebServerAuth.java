package systems.reformcloud.reformcloud2.executor.api.common.restapi.auth.basic;

import io.netty.channel.ChannelHandlerContext;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.api.database.DatabaseSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.Configurable;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.auth.Auth;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.WebRequester;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.defaults.DefaultWebRequester;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.user.WebUser;
import systems.reformcloud.reformcloud2.executor.api.common.utility.function.Double;

public class DefaultWebServerAuth implements Auth {

  public DefaultWebServerAuth(DatabaseSyncAPI api) { this.api = api; }

  private final DatabaseSyncAPI api;

  @Nonnull
  @Override
  public Double<Boolean, WebRequester>
  handleAuth(@Nonnull Configurable<JsonConfiguration> configurable,
             @Nonnull ChannelHandlerContext channelHandlerContext) {
    String userName = configurable.getString("name");
    String token = configurable.getString("token");
    if (userName.trim().isEmpty() || token.trim().isEmpty()) {
      return new Double<>(false, null);
    }

    if (!api.contains("internal_users", userName)) {
      return new Double<>(false, null);
    }

    WebUser webUser = api.find("internal_users", userName, null,
                               config -> config.get("user", WebUser.TYPE));
    if (webUser == null || !token.equals(webUser.getToken())) {
      return new Double<>(false, null);
    }

    return new Double<>(
        true, new DefaultWebRequester(channelHandlerContext, webUser.getName(),
                                      webUser.getPermissions()));
  }
}
