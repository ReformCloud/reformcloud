package systems.reformcloud.reformcloud2.executor.controller.network.channel;

import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.controller.ControllerExecutor;
import systems.reformcloud.reformcloud2.executor.controller.process.ClientManager;

import java.util.function.BiConsumer;

public class ControllerNetworkSuccessHandler implements BiConsumer<ChannelHandlerContext, Packet> {

    @Override
    public void accept(ChannelHandlerContext channelHandlerContext, Packet packet) {
        channelHandlerContext.channel().writeAndFlush(new JsonPacket(
                -511, new JsonConfiguration().add("access", true).add("name", "Controller")
        )).syncUninterruptibly();

        ProcessInformation process = ControllerExecutor.getInstance().getProcessManager().getProcess(packet.content().getString("name"));
        if (process == null) {
            System.out.println(LanguageManager.get("client-connected", packet.content().getString("name")));
            ClientManager.INSTANCE.connectClient(packet.content().get("info", ClientRuntimeInformation.TYPE));
        } else {
            process.getNetworkInfo().setConnected(true);
            process.getProcessDetail().setProcessState(ProcessState.READY);
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(process);

            System.out.println(LanguageManager.get("process-connected", process.getProcessDetail().getName(), process.getProcessDetail().getParentName()));
        }
    }
}
