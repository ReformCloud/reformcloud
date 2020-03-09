package systems.reformcloud.reformcloud2.executor.api.common.utils;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import systems.reformcloud.reformcloud2.executor.api.common.utility.PortUtil;

import java.net.InetSocketAddress;
import java.net.ServerSocket;

public class PortValidatorTest {

    @Test
    public void testPortValidator() {
        Assert.assertEquals(65535, PortUtil.checkPort(65535));
        Assume.assumeThat(PortUtil.checkPort(-1), new HigherOrEqualMatcher(0));

        int port = PortUtil.checkPort(25565);
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(port));
        } catch (final Throwable throwable) {
            throw new AssertionError("Unable to bind to " + port, throwable);
        }
    }

    private static class HigherOrEqualMatcher extends BaseMatcher<Integer> {

        public HigherOrEqualMatcher(int min) {
            this.min = min;
        }

        private final int min;

        @Override
        public boolean matches(Object o) {
            return o instanceof Integer && (int) o >= min;
        }

        @Override
        public void describeTo(Description description) {
        }
    }
}
