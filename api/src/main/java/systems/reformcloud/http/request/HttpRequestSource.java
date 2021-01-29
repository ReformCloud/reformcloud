/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
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
package systems.reformcloud.http.request;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.http.websocket.request.SocketFrameSource;

import java.io.Closeable;
import java.net.SocketAddress;
import java.util.Optional;

/**
 * Represents a source of a HTTP connection.
 *
 * @author derklaro
 * @since 25. October 2020
 */
public interface HttpRequestSource extends Closeable {

  /**
   * Get the globally unique identifier of this request source.
   *
   * @return the globally unique identifier of this request source.
   */
  @NotNull
  String id();

  /**
   * Get if this source connection is open and may get active later.
   *
   * @return if this source connection is open and may get active later.
   */
  boolean isOpen();

  /**
   * Get if this source connection is still active and connected.
   *
   * @return if this source connection is still active and connected.
   */
  boolean isActive();

  /**
   * Get if the I/O thread will perform any write action immediately.
   *
   * @return if the I/O thread will perform any write action immediately.
   */
  boolean isWritable();

  /**
   * Get the bytes before the channel is not writeable anymore so the I/O thread will
   * not perform any write action immediately. If the channel is unwriteable this is
   * {@code 0}.
   *
   * @return the bytes before the channel is not writeable anymore.
   */
  long bytesBeforeUnwritable();

  /**
   * Get the bytes before the channel is writeable again so the I/O thread will
   * perform any write action immediately. If the channel is writeable this is
   * {@code 0}.
   *
   * @return the bytes before the channel is writeable again.
   */
  long bytesBeforeWritable();

  /**
   * Get the address the channel is bound to.
   *
   * @return the address the channel is bound to.
   */
  @NotNull
  SocketAddress serverAddress();

  /**
   * Get the client address the connection is from.
   *
   * @return the client address the connection is from.
   */
  @NotNull
  SocketAddress clientAddress();

  /**
   * Upgrades this HTTP request source to a socket frame source. If the client does not send
   * a version header the version {@code 00} is assumed. If the client sends an unsupported
   * version as header, this method will return an empty optional as we cannot decode the
   * messages sent by the client.
   *
   * @return the upgraded socket frame source build on top of this HTTP request source.
   */
  @NotNull
  Optional<SocketFrameSource> upgrade();

  /**
   * Closes the connection to this source.
   */
  @Override
  void close();
}
