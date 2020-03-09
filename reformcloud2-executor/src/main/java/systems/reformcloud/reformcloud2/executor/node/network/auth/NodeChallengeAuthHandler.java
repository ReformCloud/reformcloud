package systems.reformcloud.reformcloud2.executor.node.network.auth;

import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.provider.ChallengeProvider;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.shared.ServerChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

import java.util.function.BiConsumer;

public class NodeChallengeAuthHandler extends ServerChallengeAuthHandler {

    public NodeChallengeAuthHandler(ChallengeProvider provider, BiConsumer<ChannelHandlerContext, Packet> afterSuccess) {
        super(provider, afterSuccess, name -> {
            ProcessInformation processInformation = NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().getLocalCloudProcess(name);
            return processInformation == null ? NodeExecutor.getInstance().getNodeConfig().getName() : "Controller";
        });
    }
}
