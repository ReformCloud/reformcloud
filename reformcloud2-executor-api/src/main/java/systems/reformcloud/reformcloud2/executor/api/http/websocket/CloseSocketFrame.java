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
package systems.reformcloud.reformcloud2.executor.api.http.websocket;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a socket frame which indicates the close of the connection.
 *
 * @param <T> the type of the implementing api
 * @author derklaro
 * @see SocketFrame#closeFrame(int, String)
 * @since 27. October 2020
 */
public interface CloseSocketFrame<T extends CloseSocketFrame<T>> extends SocketFrame<T> {

  /**
   * Get the status code why the connection was closed.
   *
   * @return the status code why the connection was closed.
   */
  int statusCode();

  /**
   * Sets the status code why the connection was closed.
   *
   * @param statusCode the status code why the connection was closed.
   * @return the same instance of this class, for chaining
   */
  @NotNull
  T statusCode(int statusCode);

  /**
   * Get the status code as a formatted object.
   *
   * @return the status code as a formatted object.
   */
  @Nullable
  default CloseStatus status() {
    return CloseStatus.fromCode(this.statusCode());
  }

  /**
   * Sets the status code why the connection was closed.
   *
   * @param status the status why the connection was closed.
   * @return the same instance of this class, for chaining
   */
  @NotNull
  default T status(@NotNull CloseStatus status) {
    return this.statusCode(status.code());
  }

  /**
   * Get the reason text why the connection was closed or empty if there was no reason provided.
   *
   * @return the reason why the connection were closed.
   */
  @NotNull
  String reasonText();

  /**
   * Sets the reason why the connection was closed or empty if there is no reason.
   *
   * @param reasonText the reason why the connection was closed.
   * @return the same instance of this class, for chaining
   */
  @NotNull
  T reasonText(@NotNull String reasonText);

  /**
   * {@inheritDoc}
   */
  @NotNull
  @Override
  default SocketFrameType type() {
    return SocketFrameType.CLOSE;
  }

  /**
   * WebSocket status codes specified in RFC-6455 (The WebSocket Protocol, December 2011).
   */
  enum CloseStatus {
    /**
     * Successful operation / regular socket shutdown
     */
    NORMAL_CLOSE(1000),
    /**
     * Client is leaving (browser tab closing)
     */
    CLOSING_GOING_AWAY(1001),
    /**
     * Endpoint received a malformed frame
     */
    CLOSE_PROTOCOL_ERROR(1002),
    /**
     * Endpoint received an unsupported frame (e.g. binary-only endpoint received text frame)
     */
    CLOSE_UNSUPPORTED(1003),
    /**
     * Reserved
     */
    RESERVED_UNUSED(1004),
    /**
     * Expected close status, received none
     */
    CLOSED_NO_STATUS(1005),
    /**
     * No close code frame has been received
     */
    CLOSE_ABNORMAL(1006),
    /**
     * Endpoint received inconsistent message (e.g. malformed UTF-8)
     */
    UNSUPPORTED_PAYLOAD(1007),
    /**
     * Generic code used for situations other than 1003 and 1009
     */
    POLICY_VIOLATION(1008),
    /**
     * Endpoint won't process large frame
     */
    CLOSE_TOO_LARGE(1009),
    /**
     * Client wanted an extension which server did not negotiate
     */
    MANDATORY_EXTENSION(1010),
    /**
     * Internal server error while operating
     */
    INTERNAL_SERVER_ERROR(1011),
    /**
     * Server/service is restarting
     */
    SERVICE_RESTART(1012),
    /**
     * Temporary server condition forced blocking client's request
     */
    TRY_AGAIN_LATER(1013),
    /**
     * Server acting as gateway received an invalid response
     */
    BAD_GATEWAY(1014),
    /**
     * Transport Layer Security handshake failure
     */
    TLS_HANDSHAKE_FAIL(1015);

    private static final CloseStatus[] VALUES = values(); // prevent copy
    private final int code;

    CloseStatus(int code) {
      this.code = code;
    }

    @Nullable
    public static CloseStatus fromCode(int code) {
      for (CloseStatus value : VALUES) {
        if (value.code == code) {
          return value;
        }
      }

      return null;
    }

    public int code() {
      return this.code;
    }
  }
}
