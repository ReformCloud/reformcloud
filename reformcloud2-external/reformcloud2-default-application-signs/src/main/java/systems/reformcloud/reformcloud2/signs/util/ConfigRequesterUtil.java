package systems.reformcloud.reformcloud2.signs.util;

import java.util.function.Consumer;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.signs.packets.api.out.APIPacketOutRequestSignLayouts;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignConfig;

public class ConfigRequesterUtil {

  private ConfigRequesterUtil() { throw new UnsupportedOperationException(); }

  public static void requestSignConfigAsync(Consumer<SignConfig> callback) {
    Task.EXECUTOR.execute(() -> {
      while (!DefaultChannelManager.INSTANCE.get("Controller").isPresent()) {
        AbsoluteThread.sleep(200);
      }

      DefaultChannelManager.INSTANCE.get("Controller")
          .ifPresent(
              e
              -> ExecutorAPI.getInstance()
                     .getPacketHandler()
                     .getQueryHandler()
                     .sendQueryAsync(e, new APIPacketOutRequestSignLayouts())
                     .onComplete(r -> {
                       SignConfig config =
                           r.content().get("config", SignConfig.TYPE);
                       if (config == null) {
                         return;
                       }

                       callback.accept(config);
                     }));
    });
  }
}
