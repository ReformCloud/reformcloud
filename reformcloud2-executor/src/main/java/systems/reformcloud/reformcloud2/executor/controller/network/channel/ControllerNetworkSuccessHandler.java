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
