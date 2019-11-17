package systems.reformcloud.reformcloud2.signs.application;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.api.Application;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;
import systems.reformcloud.reformcloud2.signs.application.packets.in.PacketInCreateSign;
import systems.reformcloud.reformcloud2.signs.application.packets.in.PacketInDeleteSign;
import systems.reformcloud.reformcloud2.signs.application.packets.in.PacketInGetSignConfig;
import systems.reformcloud.reformcloud2.signs.packets.PacketUtil;
import systems.reformcloud.reformcloud2.signs.util.SignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignConfig;

import java.util.Collection;
import java.util.Collections;

public class ReformCloudApplication extends Application {

    private static SignConfig signConfig;

    @Override
    public void onEnable() {
        if (!dataFolder().exists()) {
            SystemHelper.createDirectory(dataFolder().toPath());
            ConfigHelper.createDefault(dataFolder().getPath());
        }

        ExecutorAPI.getInstance().createDatabase(SignSystemAdapter.table);
        if (!ExecutorAPI.getInstance().contains(SignSystemAdapter.table, "signs")) {
            ExecutorAPI.getInstance().insert(
                    SignSystemAdapter.table,
                    "signs",
                    null,
                    new JsonConfiguration().add("signs", Collections.emptyList())
            );
        }

        ExecutorAPI.getInstance().getPacketHandler().registerNetworkHandlers(
                new PacketInCreateSign(),
                new PacketInDeleteSign(),
                new PacketInGetSignConfig()
        );

        signConfig = ConfigHelper.read(dataFolder().getPath());
    }

    @Override
    public void onDisable() {
        ExecutorAPI.getInstance().getPacketHandler().unregisterNetworkHandlers(PacketUtil.SIGN_BUS + 1);
        ExecutorAPI.getInstance().getPacketHandler().unregisterNetworkHandlers(PacketUtil.SIGN_BUS + 2);
        ExecutorAPI.getInstance().getPacketHandler().unregisterNetworkHandlers(PacketUtil.SIGN_BUS + 3);
    }

    // ====

    public static SignConfig getSignConfig() {
        return signConfig;
    }

    // ====

    public static void insert(CloudSign cloudSign) {
        Collection<CloudSign> signs = read();
        if (signs == null) {
            return;
        }

        signs.add(cloudSign);
        insert(signs);
    }

    public static void delete(CloudSign cloudSign) {
        Collection<CloudSign> signs = read();
        if (signs == null) {
            return;
        }

        Links.filterToReference(signs, e -> e.getLocation().equals(cloudSign.getLocation())).ifPresent(signs::remove);
        insert(signs);
    }

    // ====

    private static Collection<CloudSign> read() {
        return ExecutorAPI.getInstance().find(SignSystemAdapter.table, "signs", null, k -> k.get("signs", new TypeToken<Collection<CloudSign>>() {
        }));
    }

    private static void insert(Collection<CloudSign> signs) {
        ExecutorAPI.getInstance().update(SignSystemAdapter.table, "signs", new JsonConfiguration().add("signs", signs));
    }
}
