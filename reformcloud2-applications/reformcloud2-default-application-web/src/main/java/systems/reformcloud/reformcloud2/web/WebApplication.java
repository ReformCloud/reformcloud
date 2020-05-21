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
package systems.reformcloud.reformcloud2.web;

import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.api.Application;
import systems.reformcloud.reformcloud2.executor.api.common.application.updater.ApplicationUpdateRepository;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.RequestListenerHandler;
import systems.reformcloud.reformcloud2.executor.controller.ControllerExecutor;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.web.commands.WebCommand;
import systems.reformcloud.reformcloud2.web.tokens.TokenDatabase;
import systems.reformcloud.reformcloud2.web.tokens.TokenWebServerAuth;

public class WebApplication extends Application {

    public static final WebCommand WEB_COMMAND = new WebCommand();

    public static RequestListenerHandler getListener() {
        return ExecutorAPI.getInstance().getType().equals(ExecutorType.NODE)
                ? NodeExecutor.getInstance().getRequestListenerHandler()
                : ControllerExecutor.getInstance().getRequestListenerHandler();
    }

    @Override
    public void onLoad() {
        TokenDatabase.load();
        getListener().setAuth(new TokenWebServerAuth());
    }

    @Nullable
    @Override
    public ApplicationUpdateRepository getUpdateRepository() {
        return null;
    }
}
