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
package systems.reformcloud.reformcloud2.executor.api.common.restapi.request.defaults;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionResult;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.WebRequester;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;

import java.util.Collection;

public class DefaultWebRequester implements WebRequester {

    public DefaultWebRequester(ChannelHandlerContext context, String name, Collection<String> permissions) {
        this.context = context;
        this.name = name;
        this.permissions = Streams.toLowerCase(permissions);
    }

    private final ChannelHandlerContext context;

    private final String name;

    private final Collection<String> permissions;

    @NotNull
    @Override
    public Channel channel() {
        return context.channel();
    }

    @Override
    public boolean isConnected() {
        return context != null && context.channel().isOpen();
    }

    @NotNull
    @Override
    public PermissionResult hasPermissionValue(@NotNull String perm) {
        String matched = Streams.filter(permissions, permission -> {
            if (permission.equals("*")) {
                return true;
            }

            if (permission.startsWith("-")) {
                permission = permission.replaceFirst("-", "");
            }

            return perm.toLowerCase().equals(permission);
        });
        return matched == null ? PermissionResult.NOT_SET : matched.startsWith("-") ? PermissionResult.DENIED : PermissionResult.ALLOWED;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }
}
