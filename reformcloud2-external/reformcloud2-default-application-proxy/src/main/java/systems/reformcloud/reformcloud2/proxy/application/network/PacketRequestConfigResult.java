package systems.reformcloud.reformcloud2.proxy.application.network;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.query.QueryResultPacket;
import systems.reformcloud.reformcloud2.proxy.ProxyConfiguration;

public class PacketRequestConfigResult extends QueryResultPacket {

    public PacketRequestConfigResult() {
    }

    public PacketRequestConfigResult(ProxyConfiguration proxyConfiguration) {
        this.proxyConfiguration = proxyConfiguration;
    }

    private ProxyConfiguration proxyConfiguration;

    public ProxyConfiguration getProxyConfiguration() {
        return proxyConfiguration;
    }

    @Override
    public int getId() {
        return NetworkUtil.EXTERNAL_BUS + 6;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeObject(this.proxyConfiguration);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.proxyConfiguration = buffer.readObject(ProxyConfiguration.class);
    }
}
