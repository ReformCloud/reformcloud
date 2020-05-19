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
package systems.reformcloud.reformcloud2.executor.api.common.api.messaging;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.util.ErrorReportHandling;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.util.ReceiverType;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;

public interface MessageSyncAPI {

    /**
     * Sends a channel message to the specified sender
     *
     * @param receiver          The receiver of the channel message
     * @param jsonConfiguration The content which should get sent
     */
    default void sendChannelMessageSync(@NotNull String receiver, @NotNull JsonConfiguration jsonConfiguration) {
        this.sendChannelMessageSync(jsonConfiguration, ErrorReportHandling.NOTHING, receiver);
    }

    /**
     * Sends a channel message to a bunch of receivers
     *
     * @param jsonConfiguration The content which should get sent
     * @param receivers         The receivers of the channel message
     */
    default void sendChannelMessageSync(@NotNull JsonConfiguration jsonConfiguration, @NotNull String... receivers) {
        this.sendChannelMessageSync(jsonConfiguration, ErrorReportHandling.NOTHING, receivers);
    }

    /**
     * Sends a channel message to a bunch of receivers and specifies the error handling if a specified
     * receiver is not available in the network
     *
     * @param jsonConfiguration   The content which should get sent
     * @param errorReportHandling The handling type of errors if a channel is not available in the network
     * @param receivers           The receivers of the channel message
     */
    default void sendChannelMessageSync(@NotNull JsonConfiguration jsonConfiguration,
                                        @NotNull ErrorReportHandling errorReportHandling,
                                        @NotNull String... receivers) {
        this.sendChannelMessageSync(jsonConfiguration, "unknown", errorReportHandling, receivers);
    }

    /**
     * Sends a channel message to a bunch of receivers and specifies the error handling if a specified
     * receiver is not available in the network
     *
     * @param jsonConfiguration   The content which should get sent
     * @param baseChannel         The base channel through which the message got sent
     * @param errorReportHandling The handling type of errors if a channel is not available in the network
     * @param receivers           The receivers of the channel message
     */
    default void sendChannelMessageSync(
            @NotNull JsonConfiguration jsonConfiguration,
            @NotNull String baseChannel,
            @NotNull ErrorReportHandling errorReportHandling,
            @NotNull String... receivers) {
        this.sendChannelMessageSync(jsonConfiguration, baseChannel, "unknown", errorReportHandling, receivers);
    }

    /**
     * Sends a channel message to a bunch of receivers and specifies the error handling if a specified
     * receiver is not available in the network
     *
     * @param jsonConfiguration   The content which should get sent
     * @param baseChannel         The base channel through which the message got sent
     * @param subChannel          The channel identifier through which the message comes
     * @param errorReportHandling The handling type of errors if a channel is not available in the network
     * @param receivers           The receivers of the channel message
     */
    void sendChannelMessageSync(
            @NotNull JsonConfiguration jsonConfiguration,
            @NotNull String baseChannel,
            @NotNull String subChannel,
            @NotNull ErrorReportHandling errorReportHandling,
            @NotNull String... receivers
    );

    /**
     * Sends a channel message to all receivers which equals to the receiver type filter
     *
     * @param configuration The content which should get sent
     * @param receiverTypes The type of receivers which should get the message
     */
    default void sendChannelMessageSync(@NotNull JsonConfiguration configuration, @NotNull ReceiverType... receiverTypes) {
        this.sendChannelMessageSync(configuration, "unknown", receiverTypes);
    }

    /**
     * Sends a channel message to all receivers which equals to the receiver type filter
     *
     * @param configuration The content which should get sent
     * @param baseChannel   The base channel through which the message got sent
     * @param receiverTypes The type of receivers which should get the message
     */
    default void sendChannelMessageSync(@NotNull JsonConfiguration configuration,
                                        @NotNull String baseChannel,
                                        @NotNull ReceiverType... receiverTypes) {
        this.sendChannelMessageSync(configuration, baseChannel, "unknown", receiverTypes);
    }

    /**
     * Sends a channel message to all receivers which equals to the receiver type filter
     *
     * @param configuration The content which should get sent
     * @param baseChannel   The base channel through which the message got sent
     * @param subChannel    The channel identifier through which the message comes
     * @param receiverTypes The type of receivers which should get the message
     */
    void sendChannelMessageSync(@NotNull JsonConfiguration configuration, @NotNull String baseChannel,
                                @NotNull String subChannel, @NotNull ReceiverType... receiverTypes);
}
