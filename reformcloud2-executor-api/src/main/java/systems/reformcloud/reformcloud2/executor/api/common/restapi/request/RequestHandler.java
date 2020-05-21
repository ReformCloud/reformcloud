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
package systems.reformcloud.reformcloud2.executor.api.common.restapi.request;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface RequestHandler {

    /**
     * @return The path of the the web handler
     */
    String path();

    /**
     * Handles the request
     *
     * @param webRequester The web requester of this http request
     * @param in           The http request itself
     * @param response     The response handler of any response which should get sent to the requester
     */
    void handleRequest(@NotNull WebRequester webRequester, @NotNull HttpRequest in, @NotNull Consumer<HttpResponse> response);

    /**
     * Checks if a web requester has access to the current handler
     *
     * @param requester The requester who want's access to this handler
     * @return If the web requester is permitted to use this request handler
     */
    boolean canAccess(@NotNull WebRequester requester);
}
