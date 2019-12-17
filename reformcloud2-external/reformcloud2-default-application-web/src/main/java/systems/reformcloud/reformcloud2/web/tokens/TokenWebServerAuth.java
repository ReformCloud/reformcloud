package systems.reformcloud.reformcloud2.web.tokens;

import io.netty.channel.ChannelHandlerContext;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.Configurable;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.auth.Auth;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.WebRequester;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.defaults.DefaultWebRequester;
import systems.reformcloud.reformcloud2.executor.api.common.utility.function.Double;
import systems.reformcloud.reformcloud2.web.WebApplication;

public class TokenWebServerAuth implements Auth {

  private final Map<Long, WebRequester> handled = new ConcurrentHashMap<>();

  @Nonnull
  @Override
  public Double<Boolean, WebRequester>
  handleAuth(@Nonnull Configurable<JsonConfiguration> configurable,
             @Nonnull ChannelHandlerContext channelHandlerContext) {
    String token = configurable.getOrDefault("token", (String)null);
    if (token == null) {
      if (TokenDatabase.isSetupDone()) {
        return new Double<>(false, null);
      }

      long current = System.currentTimeMillis();
      if (!WebApplication.WEB_COMMAND.tryAwait(
              current, this,
              new SetupWebRequester(channelHandlerContext, "rc_setup"))) {
        System.err.println("Another setup is already running");
        return new Double<>(false, null);
      }

      System.out.println(
          "Please verify that you've send the request to setup the web portal by typing \"web verify\"... (Request times out in 15 sec)");
      while (!handled.containsKey(current)) {
        if (current + TimeUnit.SECONDS.toMillis(15) <=
            System.currentTimeMillis()) {
          break;
        }
      }

      WebRequester requester = handled.remove(current);
      return new Double<>(requester != null, requester);
    }

    if (TokenDatabase.tryAuth(token)) {
      WebRequester webRequester =
          new DefaultWebRequester(channelHandlerContext, "reformcloud_internal",
                                  Collections.singletonList("*"));
      return new Double<>(true, webRequester);
    }

    return new Double<>(false, null);
  }

  public void complete(long time, WebRequester result) {
    if (time + TimeUnit.SECONDS.toMillis(15) <= System.currentTimeMillis()) {
      return;
    }

    this.handled.put(time, result);
  }
}
