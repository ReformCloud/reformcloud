/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.reformcloud.reformcloud2.node.http.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.http.websocket.CloseSocketFrame;
import systems.reformcloud.reformcloud2.node.http.utils.BinaryUtils;

import java.nio.charset.StandardCharsets;

public class DefaultCloseSocketFrame extends DefaultSocketFrame<DefaultCloseSocketFrame> implements CloseSocketFrame<DefaultCloseSocketFrame> {

    private int statusCode;
    private String statusText;

    public DefaultCloseSocketFrame(int rsv, boolean finalFragment, int statusCode, String statusText) {
        super(rsv, finalFragment, newData(statusCode, statusText));
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    @NotNull
    private static byte[] newData(int statusCode, @NotNull String reasonText) {
        ByteBuf binaryData = Unpooled.buffer(2 + reasonText.length()).writeShort(statusCode);
        if (!reasonText.isEmpty()) {
            binaryData.writeCharSequence(reasonText, StandardCharsets.UTF_8);
        }

        binaryData.readerIndex(0);
        return BinaryUtils.binaryArrayFromByteBuf(binaryData);
    }

    @Override
    public int statusCode() {
        return this.statusCode;
    }

    @Override
    public @NotNull DefaultCloseSocketFrame statusCode(int statusCode) {
        this.statusCode = statusCode;
        super.content(newData(this.statusCode, this.statusText));
        return this;
    }

    @Override
    public @NotNull String reasonText() {
        return this.statusText;
    }

    @Override
    public @NotNull DefaultCloseSocketFrame reasonText(@NotNull String reasonText) {
        this.statusText = reasonText;
        super.content(newData(this.statusCode, this.statusText));
        return this;
    }

    @Override
    public @NotNull DefaultCloseSocketFrame self() {
        return this;
    }
}
