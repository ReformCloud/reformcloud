package de.klaro.reformcloud2.executor.api.common.restapi.auth.basic;

import de.klaro.reformcloud2.executor.api.common.api.database.DatabaseSyncAPI;
import de.klaro.reformcloud2.executor.api.common.configuration.Configurable;
import de.klaro.reformcloud2.executor.api.common.restapi.auth.Auth;
import de.klaro.reformcloud2.executor.api.common.restapi.request.WebRequester;
import de.klaro.reformcloud2.executor.api.common.utility.function.Double;
import io.netty.channel.ChannelHandlerContext;

public class DefaultWebServerAuth implements Auth {
    
    public DefaultWebServerAuth(DatabaseSyncAPI api) {
        this.api = api;
    }

    private final DatabaseSyncAPI api;

    @Override
    public Double<Boolean, WebRequester> handleAuth(Configurable configurable, ChannelHandlerContext channelHandlerContext) {
        throw new UnsupportedOperationException("Not supported yet");
    }
}
