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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import systems.reformcloud.network.data.ProtocolBuffer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class DefaultNetworkAddress implements NetworkAddress {

  private String host;
  private int port;
  //
  private transient InetAddress inetAddress;
  private transient InetSocketAddress inetSocketAddress;

  protected DefaultNetworkAddress() {
  }

  @ApiStatus.Internal
  public DefaultNetworkAddress(String host, int port) {
    this.host = host;
    this.port = port;
  }

  @Override
  public @NotNull String getHost() {
    return this.host;
  }

  @Override
  public void setHost(@NotNull String host) {
    this.host = host;
  }

  @Override
  public @Range(from = 0, to = 65535) int getPort() {
    return this.port;
  }

  @Override
  public void setPort(@Range(from = 0, to = 65535) int port) {
    this.port = port;
  }

  @Override
  public @NotNull InetAddress toInetAddress() {
    if (this.inetAddress == null) {
      try {
        this.inetAddress = InetAddress.getByName(this.host);
      } catch (UnknownHostException exception) {
        throw new IllegalStateException("Unable to resolve host " + this.host, exception);
      }
    }
    return this.inetAddress;
  }

  @Override
  public @NotNull InetSocketAddress toInetSocketAddress() {
    if (this.inetSocketAddress == null) {
      this.inetSocketAddress = new InetSocketAddress(this.toInetAddress(), this.port);
    }
    return this.inetSocketAddress;
  }

  @Override
  public @NotNull NetworkAddress clone() {
    return new DefaultNetworkAddress(this.host, this.port);
  }

  @Override
  public void write(@NotNull ProtocolBuffer buffer) {
    buffer.writeString(this.host);
    buffer.writeInt(this.port);
  }

  @Override
  public void read(@NotNull ProtocolBuffer buffer) {
    this.host = buffer.readString();
    this.port = buffer.readInt();
  }
}
