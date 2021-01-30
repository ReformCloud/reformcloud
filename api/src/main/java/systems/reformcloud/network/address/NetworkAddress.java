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
package systems.reformcloud.network.address;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import systems.reformcloud.network.data.SerializableObject;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;

/**
 * A network address.
 */
public interface NetworkAddress extends SerializableObject, Cloneable {

  /**
   * Creates a new network address.
   *
   * @param uri The uri to create the address from.
   * @return The created address.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  static NetworkAddress fromUri(@NotNull URI uri) {
    return address(uri.getHost(), uri.getPort());
  }

  /**
   * Creates a new network address.
   *
   * @param inetSocketAddress The InetSocketAddress to create the address from.
   * @return The created address.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  static NetworkAddress fromInetSocketAddress(@NotNull InetSocketAddress inetSocketAddress) {
    return fromInetAddress(inetSocketAddress.getAddress(), inetSocketAddress.getPort());
  }

  /**
   * Creates a new network address.
   *
   * @param inetAddress The InetAddress to read the host from.
   * @param port        The port of the address.
   * @return The created address.
   */
  @NotNull
  @Contract(value = "_, _ -> new", pure = true)
  static NetworkAddress fromInetAddress(@NotNull InetAddress inetAddress, @Range(from = 0, to = 65535) int port) {
    return address(inetAddress.getHostAddress(), port);
  }

  /**
   * Creates a new network address.
   *
   * @param host The host of the address.
   * @param port The port of the address.
   * @return The created address.
   */
  @NotNull
  @Contract(value = "_, _ -> new", pure = true)
  static NetworkAddress address(@NotNull String host, @Range(from = 0, to = 65535) int port) {
    return new DefaultNetworkAddress(host, port);
  }

  /**
   * Gets the host of the address.
   *
   * @return The host of the address.
   */
  @NotNull
  String getHost();

  /**
   * Sets the host of the address.
   *
   * @param host The host of the address.
   */
  void setHost(@NotNull String host);

  /**
   * Gets the port of the address.
   *
   * @return The port of the address.
   */
  @Range(from = 0, to = 65535)
  int getPort();

  /**
   * Sets the port of the address.
   *
   * @param port The port of the address.
   */
  void setPort(@Range(from = 0, to = 65535) int port);

  /**
   * Creates an {@link InetAddress} from this address.
   *
   * @return The created address from this wrapper.
   */
  @NotNull
  InetAddress toInetAddress();

  /**
   * Creates an {@link InetSocketAddress} from this address.
   *
   * @return The created address from this wrapper.
   */
  @NotNull
  InetSocketAddress toInetSocketAddress();

  /**
   * Creates a clone of this address.
   *
   * @return A clone of this address.
   */
  @NotNull
  NetworkAddress clone();
}
