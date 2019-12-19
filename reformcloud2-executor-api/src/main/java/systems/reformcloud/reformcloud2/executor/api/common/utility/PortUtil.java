package systems.reformcloud.reformcloud2.executor.api.common.utility;

import java.net.InetSocketAddress;
import java.net.ServerSocket;

public final class PortUtil {

  private PortUtil() { throw new UnsupportedOperationException(); }

  /**
   * Checks if a specific port is free
   *
   * @param startPort The port which should be checked and counted up
   * @return The next free port
   */
  public static int checkPort(int startPort) {
    while (isPortInUse(startPort)) {
      startPort++;
    }

    return startPort;
  }

  private static boolean isPortInUse(int port) {
    try (ServerSocket serverSocket = new ServerSocket()) {
      serverSocket.bind(new InetSocketAddress(port));
      return false;
    } catch (final Throwable throwable) {
      return true;
    }
  }
}
