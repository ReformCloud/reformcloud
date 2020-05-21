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
package systems.reformcloud.reformcloud2.executor;

import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.client.ClientLauncher;
import systems.reformcloud.reformcloud2.executor.controller.ControllerLauncher;
import systems.reformcloud.reformcloud2.executor.node.NodeLauncher;

public final class ExecutorChooser {

    public static synchronized void main(String[] args) {
        ExecutorType executor = ExecutorType.getByID(toID(System.getProperty("reformcloud.executor.type", "-1")));
        if (!executor.isSupported()) {
            throw new RuntimeException("Unsupported executor used!");
        }

        switch (executor) {
            case CONTROLLER: {
                ControllerLauncher.main(args);
                break;
            }

            case CLIENT: {
                ClientLauncher.main(args);
                break;
            }

            case NODE: {
                NodeLauncher.main(args);
                break;
            }
        }
    }

    private static Integer toID(String convert) {
        try {
            return Integer.parseInt(convert);
        } catch (final Throwable throwable) {
            //May cause if system variable is not set properly
            return -1;
        }
    }
}
