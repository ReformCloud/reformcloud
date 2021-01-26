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
package systems.reformcloud.reformcloud2.examples.network;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.event.events.messaging.ChannelMessageReceiveEvent;
import systems.reformcloud.reformcloud2.executor.api.event.handler.Listener;

public class ExampleChannelMessageHandling {

  // Sends a channel message to all nodes and processes
  public static void sendCustomChannelMessage() {
    ExecutorAPI.getInstance().getChannelMessageProvider().publishChannelMessage(
      "testChannel", // The name of the channel which is accessible for better identifying of the message
      JsonConfiguration.newJsonConfiguration().add("extra", "hello") // The data which should get sent to the network components
    );
  }

  // Handles the receive of a custom channel message. Do not forgot to register the listener.
  @Listener
  public void handle(final @NotNull ChannelMessageReceiveEvent event) {
    // Checks if the base channel is the same as the message was sent to
    if (!event.getChannel().equals("testChannel")) {
      return;
    }

    System.out.println(event.getData().get("extra")); // print the message which was sent in the json config
  }
}
