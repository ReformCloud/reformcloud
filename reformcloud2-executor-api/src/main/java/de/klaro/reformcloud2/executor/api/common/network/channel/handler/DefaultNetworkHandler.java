package de.klaro.reformcloud2.executor.api.common.network.channel.handler;

public abstract class DefaultNetworkHandler implements NetworkHandler {

    public DefaultNetworkHandler(int id) {
        this.id = id;
    }

    private final int id;

    @Override
    public int getHandlingPacketID() {
        return id;
    }
}
