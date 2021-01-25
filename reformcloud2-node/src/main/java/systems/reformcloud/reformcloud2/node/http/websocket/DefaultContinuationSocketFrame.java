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

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.http.websocket.ContinuationSocketFrame;

import java.nio.charset.StandardCharsets;

public class DefaultContinuationSocketFrame extends DefaultSocketFrame<DefaultContinuationSocketFrame> implements ContinuationSocketFrame<DefaultContinuationSocketFrame> {

  private String text;

  public DefaultContinuationSocketFrame(int rsv, boolean finalFragment, String text) {
    super(rsv, finalFragment, text.isEmpty() ? new byte[0] : text.getBytes(StandardCharsets.UTF_8));
    this.text = text;
  }

  @Override
  public @NotNull String text() {
    return this.text;
  }

  @Override
  public @NotNull DefaultContinuationSocketFrame text(@NotNull String text) {
    this.text = text;
    super.content(text.getBytes(StandardCharsets.UTF_8));
    return this;
  }

  @Override
  public @NotNull DefaultContinuationSocketFrame self() {
    return this;
  }
}
