package systems.reformcloud.reformcloud2.signs.util;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.signs.application.packets.PacketRequestSignLayouts;
import systems.reformcloud.reformcloud2.signs.application.packets.PacketRequestSignLayoutsResult;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignConfig;

import java.util.function.Consumer;

public final class ConfigRequesterUtil {

    private ConfigRequesterUtil() {
        throw new UnsupportedOperationException();
    }

    public static void requestSignConfigAsync(Consumer<SignConfig> callback) {
        Task.EXECUTOR.execute(() -> {
            while (!DefaultChannelManager.INSTANCE.get("Controller").isPresent()) {
                AbsoluteThread.sleep(200);
            }

            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(e -> ExecutorAPI.getInstance().getPacketHandler().getQueryHandler().sendQueryAsync(e, new PacketRequestSignLayouts()).onComplete(r -> {
                if (r instanceof PacketRequestSignLayoutsResult) {
                    callback.accept(((PacketRequestSignLayoutsResult) r).getSignConfig());
                }
            }));
        });
    }
}
