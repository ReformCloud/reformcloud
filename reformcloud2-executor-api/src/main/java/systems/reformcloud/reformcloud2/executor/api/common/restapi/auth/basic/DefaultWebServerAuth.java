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
package systems.reformcloud.reformcloud2.executor.api.common.restapi.auth.basic;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.api.database.DatabaseSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.auth.Auth;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.WebRequester;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.defaults.DefaultWebRequester;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.user.WebUser;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Duo;

public class DefaultWebServerAuth implements Auth {

    private final DatabaseSyncAPI api;

    public DefaultWebServerAuth(DatabaseSyncAPI api) {
        this.api = api;
    }

    @NotNull
    @Override
    public Duo<Boolean, WebRequester> handleAuth(@NotNull JsonConfiguration configurable, @NotNull ChannelHandlerContext channelHandlerContext) {
        String userName = configurable.getString("name");
        String token = configurable.getString("token");
        if (userName.trim().isEmpty() || token.trim().isEmpty()) {
            return new Duo<>(false, null);
        }

        if (!this.api.contains("internal_users", userName)) {
            return new Duo<>(false, null);
        }

        WebUser webUser = this.api.find("internal_users", userName, null, config -> config.get("user", WebUser.TYPE));
        if (webUser == null || !token.equals(webUser.getToken())) {
            return new Duo<>(false, null);
        }

        return new Duo<>(true, new DefaultWebRequester(
                channelHandlerContext, webUser.getName(), webUser.getPermissions()
        ));
    }
}
