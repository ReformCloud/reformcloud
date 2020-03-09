package systems.reformcloud.reformcloud2.executor.api.common.process.join;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.PlayerServerSwitchEvent;
import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;

public class ServerSwitchListener {

    @Listener
    public void handle(final PlayerServerSwitchEvent event) {
        OnlyProxyJoinHelper.handleServerSwitch(event.getUuid(), event.getTargetServer());
    }
}
