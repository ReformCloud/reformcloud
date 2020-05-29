/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.web.tokens;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.restapi.auth.Auth;
import systems.reformcloud.reformcloud2.executor.api.restapi.request.WebRequester;
import systems.reformcloud.reformcloud2.executor.api.restapi.request.defaults.DefaultWebRequester;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Duo;
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
            while (!this.handled.containsKey(current)) {
                if (current + TimeUnit.SECONDS.toMillis(15) <= System.currentTimeMillis()) {
                    break;
                }
            }

            WebRequester requester = this.handled.remove(current);
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
