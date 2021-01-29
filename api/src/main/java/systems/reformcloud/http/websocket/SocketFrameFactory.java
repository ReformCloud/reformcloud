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
package systems.reformcloud.http.websocket;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents a factory for frame factories.
 *
 * @author derklaro
 * @since 27. October 2020
 */
public abstract class SocketFrameFactory {
  /**
   * The default frame factory.
   */
  @ApiStatus.Internal
  protected static final AtomicReference<SocketFrameFactory> DEFAULT = new AtomicReference<>();

  /**
   * Creates a new socket frame for the given type. The returned implementation
   * is not a sub type of for example a {@link CloseSocketFrame} even if as the
   * {@code type} {@link SocketFrameType#CLOSE} is provided. For specific
   * implementation use {@link #close(int, String)}, {@link #continuation(String)}
   * and {@link #text(String)}.
   *
   * @param frameType the type of socket frame to create.
   * @return the created socket frame
   */
  @NotNull
  public abstract SocketFrame<?> forType(@NotNull SocketFrameType frameType);

  /**
   * Creates a new close socket frame.
   *
   * @param status     the status code why the connection was closed.
   * @param statusText the reason text why the connection was closed or empty.
   * @return the created close socket frame.
   */
  @NotNull
  public abstract CloseSocketFrame<?> close(int status, @NotNull String statusText);

  /**
   * Creates a new continuation frame.
   *
   * @param text the text of the frame or empty if a binary continuation frame.
   * @return the created continuation frame.
   */
  @NotNull
  public abstract ContinuationSocketFrame<?> continuation(@NotNull String text);

  /**
   * Creates a new text frame.
   *
   * @param text the text data of the frame
   * @return the created text socket frame
   */
  @NotNull
  public abstract TextSocketFrame<?> text(@NotNull String text);
}
