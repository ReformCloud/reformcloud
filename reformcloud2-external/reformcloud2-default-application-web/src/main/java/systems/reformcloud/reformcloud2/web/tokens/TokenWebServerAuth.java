package systems.reformcloud.reformcloud2.web.tokens;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.auth.Auth;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.WebRequester;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.defaults.DefaultWebRequester;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Duo;
import systems.reformcloud.reformcloud2.web.WebApplication;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class TokenWebServerAuth implements Auth {

    private final Map<Long, WebRequester> handled = new ConcurrentHashMap<>();

    @NotNull
    @Override
    public Duo<Boolean, WebRequester> handleAuth(@NotNull JsonConfiguration configurable, @NotNull ChannelHandlerContext channelHandlerContext) {
        String token = configurable.getOrDefault("token", (String) null);
        if (token == null) {
            if (TokenDatabase.isSetupDone()) {
                return new Duo<>(false, null);
            }

            long current = System.currentTimeMillis();
            if (!WebApplication.WEB_COMMAND.tryAwait(current, this, new SetupWebRequester(channelHandlerContext, "rc_setup"))) {
                System.err.println("Another setup is already running");
                return new Duo<>(false, null);
            }

            System.out.println("Please verify that you've send the request to setup the web portal by typing \"web verify\"... (Request times out in 15 sec)");
            while (!handled.containsKey(current)) {
                if (current + TimeUnit.SECONDS.toMillis(15) <= System.currentTimeMillis()) {
                    break;
                }
            }

            WebRequester requester = handled.remove(current);
            return new Duo<>(requester != null, requester);
        }

        if (TokenDatabase.tryAuth(token)) {
            WebRequester webRequester = new DefaultWebRequester(channelHandlerContext, "reformcloud_internal", Collections.singletonList("*"));
            return new Duo<>(true, webRequester);
        }

        return new Duo<>(false, null);
    }

    public void complete(long time, WebRequester result) {
        if (time + TimeUnit.SECONDS.toMillis(15) <= System.currentTimeMillis()) {
            return;
        }

        this.handled.put(time, result);
    }
}
