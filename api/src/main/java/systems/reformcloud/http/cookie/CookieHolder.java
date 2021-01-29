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
package systems.reformcloud.http.cookie;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.http.HttpInformation;

import java.util.Collection;
import java.util.Optional;

/**
 * An expanded version of a {@link HttpInformation} which holds information about cookies
 * provided to the incoming/outgoing connection.
 *
 * @param <T> the type of the implementing api
 * @author derklaro
 * @since 25. October 2020
 */
public interface CookieHolder<T extends CookieHolder<T>> extends HttpInformation<T> {

  /**
   * Get a cookie by it's name.
   *
   * @param name the name of the cookie to get.
   * @return the cookie or an empty optional if a cookie by the {@code name} does not exists.
   */
  @NotNull
  Optional<HttpCookie> cookie(@NotNull String name);

  /**
   * Get all cookies known to this holder.
   *
   * @return all cookies known to this holder.
   */
  @NotNull
  Collection<HttpCookie> cookies();

  /**
   * Adds a cookie to the known cookies of this holder.
   *
   * @param cookie the cookie to add.
   * @return the same instance of the providing type, for chaining
   */
  @NotNull
  T cookie(@NotNull HttpCookie cookie);

  /**
   * Adds cookies to the known cookies of this holder.
   *
   * @param cookies the cookies to add.
   * @return the same instance of the providing type, for chaining
   */
  @NotNull
  T cookies(@NonNls HttpCookie... cookies);

  /**
   * Adds cookies to the known cookies of this holder.
   *
   * @param cookies the cookies to add.
   * @return the same instance of the providing type, for chaining
   */
  @NotNull
  T cookies(@NotNull Collection<HttpCookie> cookies);
}
