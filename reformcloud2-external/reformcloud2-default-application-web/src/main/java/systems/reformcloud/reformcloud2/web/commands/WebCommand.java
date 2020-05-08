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
package systems.reformcloud.reformcloud2.web.commands;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.WebRequester;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Trio;
import systems.reformcloud.reformcloud2.web.tokens.TokenWebServerAuth;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class WebCommand extends GlobalCommand {

    private static Trio<Long, TokenWebServerAuth, WebRequester> waiting;

    public WebCommand() {
        super("web", "reformcloud.command.web", "The default web command", new ArrayList<>());
    }

    public boolean tryAwait(long time, TokenWebServerAuth auth, WebRequester requester) {
        if (waiting != null) {
            return false;
        }

        waiting = new Trio<>(time, auth, requester);
        return true;
    }

    @Override
    public boolean handleCommand(@NotNull CommandSource commandSource, @NotNull String[] strings) {
        if (strings.length == 1 && strings[0].equalsIgnoreCase("verify")) {
            if (waiting == null) {
                return true;
            }

            if (waiting.getFirst() + TimeUnit.SECONDS.toMillis(15) <= System.currentTimeMillis()) {
                return true;
            }

            Objects.requireNonNull(waiting.getSecond()).complete(waiting.getFirst(), waiting.getThird());
        }

        return true;
    }
}
