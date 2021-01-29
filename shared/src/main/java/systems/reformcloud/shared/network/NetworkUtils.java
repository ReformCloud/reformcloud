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
package systems.reformcloud.shared.network;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

public final class NetworkUtils {

  private NetworkUtils() {
    throw new UnsupportedOperationException();
  }

  @NotNull
  public static Collection<String> getAllAvailableIpAddresses() {
    Collection<String> result = new ArrayList<>();

    try {
      Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
      while (networkInterfaces.hasMoreElements()) {
        Enumeration<InetAddress> inetAddresses = networkInterfaces.nextElement().getInetAddresses();
        while (inetAddresses.hasMoreElements()) {
          String address = inetAddresses.nextElement().getHostAddress();
          int location = address.indexOf('%');
          if (location != -1) {
            // % is used if java can detect which internet adapter the address is from
            // It can look like: 0:0:0:0:0:0:0:0%eth2
            // We decently remove the information about the internet adapter
            address = address.substring(0, location);
          }

          if (!result.contains(address)) {
            result.add(address);
          }
        }
      }
    } catch (Throwable ignored) {
    }

    return result;
  }

  @Nullable
  public static String validateAndGetIpAddress(@NotNull String hostInput) {
    try {
      return InetAddress.getByName(hostInput).getHostAddress();
    } catch (UnknownHostException | ArrayIndexOutOfBoundsException exception) {
      return null;
    }
  }

  public static int checkAndReplacePortIfInUse(int startPort) {
    startPort = Math.max(startPort, 0);
    while (isPortInUse(startPort) && startPort < 65536) {
      startPort++;
    }

    return Math.min(startPort, 65535);
  }

  private static boolean isPortInUse(int port) {
    try (ServerSocket serverSocket = new ServerSocket()) {
      serverSocket.bind(new InetSocketAddress(port));
      return false;
    } catch (IOException exception) {
      return true;
    }
  }
}
