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
package systems.reformcloud.reformcloud2.node.sentry;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.node.NodeExecutor;

public final class SentryLoggingLoader {

  private SentryLoggingLoader() {
    throw new UnsupportedOperationException();
  }

  public static void loadSentryLogging(@NotNull NodeExecutor nodeExecutor) {
    if (!Boolean.getBoolean("systems.reformcloud.IKnownWhatIDid")) {
      if (nodeExecutor.getNodeConfig().isSendAnonymousErrorReports()) {
        printEnabledMessage();
      } else {
        printDisabledMessage();
      }
    }

    SentryErrorReporter.init(nodeExecutor);
  }

  private static void printEnabledMessage() {
    System.out.println("ReformCloud is collecting anonymous user data if an error occurs to fix them faster as before");
    System.out.println("That included the running user-name, the os version, the java-version, some memory information " +
      "and the error itself");
    System.out.println("If don't want to help us fixing bugs much faster than before, set \"sendAnonymousErrorReports\" in the node " +
      "configuration file to \"false\"");
    System.out.println("If you don't want to see this message anymore append \"-Dsystems.reformcloud.IKnownWhatIDid=true\" " +
      "to the startup script");
    System.out.println("Thank you helping us out!");
  }

  private static void printDisabledMessage() {
    System.out.println("Sending anonymous user data is disabled!");
    System.out.println("If you had changed your mind set  \"sendAnonymousErrorReports\" in the node " + "configuration file to \"true\"");
    System.out.println("If you don't want to see this message anymore append \"-Dsystems.reformcloud.IKnownWhatIDid=true\" " +
      "to the startup script");
  }
}
