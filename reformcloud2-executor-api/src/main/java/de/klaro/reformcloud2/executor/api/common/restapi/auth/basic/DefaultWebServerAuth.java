package de.klaro.reformcloud2.executor.api.common.restapi.auth.basic;

import de.klaro.reformcloud2.executor.api.common.api.database.DatabaseSyncAPI;
import de.klaro.reformcloud2.executor.api.common.configuration.Configurable;
import de.klaro.reformcloud2.executor.api.common.restapi.auth.Auth;
import de.klaro.reformcloud2.executor.api.common.restapi.request.WebRequester;
import de.klaro.reformcloud2.executor.api.common.restapi.request.defaults.DefaultWebRequester;
import de.klaro.reformcloud2.executor.api.common.restapi.user.WebUser;
import de.klaro.reformcloud2.executor.api.common.utility.function.Double;
import io.netty.channel.ChannelHandlerContext;

public class DefaultWebServerAuth implements Auth {
    
    public DefaultWebServerAuth(DatabaseSyncAPI api) {
        this.api = api;
    }

    private final DatabaseSyncAPI api;

    @Override
    public Double<Boolean, WebRequester> handleAuth(Configurable configurable, ChannelHandlerContext channelHandlerContext) {
        String userName = configurable.getString("name");
        String token = configurable.getString("token");
        if (userName.trim().isEmpty() || token.trim().isEmpty()) {
            return new Double<>(false, null);
        }

        if (!api.contains("internal_users", userName)) {
            return new Double<>(false, null);
        }

        WebUser webUser = api.find("internal_users", userName, null, config -> config.get("user", WebUser.TYPE));
        if (!token.equals(webUser.getToken())) {
            return new Double<>(false, null);
        }

        return new Double<>(true, new DefaultWebRequester(
                channelHandlerContext, webUser.getName(), webUser.getPermissions()
        ));
    }
}
