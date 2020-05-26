/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
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
package systems.reformcloud.reformcloud2.executor.api.utils;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import systems.reformcloud.reformcloud2.executor.api.utility.PortUtil;

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
            return o instanceof Integer && (int) o >= this.min;
        }

        @Override
        public void describeTo(Description description) {
        }
    }
}
