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
package systems.reformcloud.reformcloud2.executor.api.http.decode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a decode result of a http request.
 *
 * @author derklaro
 * @since 25. October 2020
 */
public class DecodeResult {
  /**
   * The success result which has no exception stack.
   */
  protected static final DecodeResult SUCCESS = new DecodeResult(null);
  /**
   * Represents a decode result which is unfinished so the decode result is still running.
   */
  protected static final DecodeResult UNFINISHED = new DecodeResult(new RuntimeException("Decode process still running"));

  private final Throwable cause;

  /**
   * Constructs a new decode result.
   *
   * @param cause the cause of the decode failure or {@code null} if the result is success.
   */
  protected DecodeResult(Throwable cause) {
    this.cause = cause;
  }

  /**
   * Constructs a new decode result.
   *
   * @param cause the cause of the decode failure or {@code null} if the result is success.
   * @return the constructed decode result or {@link DecodeResult#SUCCESS} if {@code cause} is {@code null}
   */
  @NotNull
  public static DecodeResult result(@Nullable Throwable cause) {
    return cause == null ? SUCCESS : new DecodeResult(cause);
  }

  /**
   * Get the constant success result.
   *
   * @return the constant success result.
   */
  @NotNull
  public static DecodeResult success() {
    return SUCCESS;
  }

  /**
   * Get the constant unfinished decode result.
   *
   * @return the constant unfinished result.
   */
  @NotNull
  public static DecodeResult unfinished() {
    return UNFINISHED;
  }

  /**
   * Get if this result is finished.
   *
   * @return if the decode process is finished.
   */
  public boolean isFinished() {
    return this.cause != UNFINISHED.cause;
  }

  /**
   * Get if this result was successful.
   *
   * @return if the decode process was successful.
   */
  public boolean isSuccess() {
    return this.cause == null;
  }

  /**
   * Get if this result failed.
   *
   * @return if the decode process was finished exceptionally.
   */
  public boolean isFailure() {
    return this.cause != null && this.cause != UNFINISHED.cause;
  }

  /**
   * The cause why the decode is not yet finished or successful.
   *
   * @return the cause why the decode is not yet finished or successful or {@code null} if the decode was successful.
   */
  @Nullable
  public Throwable getCause() {
    return this.cause;
  }
}
