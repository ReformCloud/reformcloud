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
package systems.reformcloud.reformcloud2.executor.controller.network.channel;

import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.packet.client.PacketOutClientChallengeResponse;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.packet.server.PacketOutServerGrantAccess;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.controller.ControllerExecutor;
import systems.reformcloud.reformcloud2.executor.controller.process.ClientManager;

import java.util.function.BiConsumer;

public class ControllerNetworkSuccessHandler implements BiConsumer<ChannelHandlerContext, PacketOutClientChallengeResponse> {

    @Override
    public void accept(ChannelHandlerContext channelHandlerContext, PacketOutClientChallengeResponse packet) {
        channelHandlerContext.channel().writeAndFlush(new PacketOutServerGrantAccess("Controller", true)).syncUninterruptibly();

        ProcessInformation process = ControllerExecutor.getInstance().getProcessManager().getProcess(packet.getName());
        if (process == null) {
            System.out.println(LanguageManager.get("client-connected", packet.getName()));
            ClientManager.INSTANCE.connectClient(packet.getExtraData().get("info", ClientRuntimeInformation.TYPE));
        } else {
            process.getNetworkInfo().setConnected(true);
            process.getProcessDetail().setProcessState(process.getProcessDetail().getInitialState());
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(process);

            System.out.println(LanguageManager.get("process-connected", process.getProcessDetail().getName(), process.getProcessDetail().getParentName()));
        }
    }
}
