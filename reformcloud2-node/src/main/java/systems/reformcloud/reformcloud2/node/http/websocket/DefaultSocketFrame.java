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
import systems.reformcloud.reformcloud2.executor.api.http.websocket.SocketFrame;
import systems.reformcloud.reformcloud2.node.http.InstanceHolder;

public abstract class DefaultSocketFrame<T extends DefaultSocketFrame<T>> implements SocketFrame<T>, InstanceHolder<T> {

  private int rsv;
  private byte[] content;
  private boolean finalFragment;

  public DefaultSocketFrame(int rsv, boolean finalFragment, byte[] content) {
    this.rsv = rsv;
    this.finalFragment = finalFragment;
    this.content = content;
  }

  @Override
  public byte[] content() {
    return this.content;
  }

  @Override
  public @NotNull T content(byte[] content) {
    this.content = content;
    return this.self();
  }

  @Override
  public boolean finalFragment() {
    return this.finalFragment;
  }

  @Override
  public @NotNull T finalFragment(boolean finalFragment) {
    this.finalFragment = finalFragment;
    return this.self();
  }

  @Override
  public int rsv() {
    return this.rsv;
  }

  @Override
  public @NotNull T rsv(int rsv) {
    this.rsv = rsv;
    return this.self();
  }
}
