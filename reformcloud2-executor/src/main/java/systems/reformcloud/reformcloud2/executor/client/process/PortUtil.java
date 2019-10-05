package systems.reformcloud.reformcloud2.executor.client.process;

import java.net.InetSocketAddress;
import java.net.ServerSocket;

public final class PortUtil {

    private PortUtil() {
    }

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
