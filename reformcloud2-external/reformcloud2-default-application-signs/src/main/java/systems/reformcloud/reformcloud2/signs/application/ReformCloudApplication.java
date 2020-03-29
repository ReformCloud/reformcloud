package systems.reformcloud.reformcloud2.signs.application;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.api.Application;
import systems.reformcloud.reformcloud2.executor.api.common.application.updater.ApplicationUpdateRepository;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;
import systems.reformcloud.reformcloud2.signs.application.listener.ProcessInclusionHandler;
import systems.reformcloud.reformcloud2.signs.application.packets.in.PacketInCreateSign;
import systems.reformcloud.reformcloud2.signs.application.packets.in.PacketInDeleteSign;
import systems.reformcloud.reformcloud2.signs.application.packets.in.PacketInGetSignConfig;
import systems.reformcloud.reformcloud2.signs.application.packets.out.PacketOutReloadConfig;
import systems.reformcloud.reformcloud2.signs.application.updater.SignsUpdater;
import systems.reformcloud.reformcloud2.signs.packets.PacketUtil;
import systems.reformcloud.reformcloud2.signs.util.SignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignConfig;

import java.util.Collection;
import java.util.Collections;

public class ReformCloudApplication extends Application {

    private static SignConfig signConfig;

    private static ReformCloudApplication instance;

    private static final ApplicationUpdateRepository REPOSITORY = new SignsUpdater();

    @Override
    public void onInstallable() {
        ExecutorAPI.getInstance().getEventManager().registerListener(new ProcessInclusionHandler());
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        if (!dataFolder().exists()) {
            SystemHelper.createDirectory(dataFolder().toPath());
            ConfigHelper.createDefault(dataFolder().getPath());
        }

        ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().createDatabase(SignSystemAdapter.table);
        if (!ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().contains(SignSystemAdapter.table, "signs")) {
            ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().insert(
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
        DefaultChannelManager.INSTANCE.getAllSender().forEach(e -> e.sendPacket(new PacketOutReloadConfig(signConfig)));
    }

    @Override
    public void onDisable() {
        ExecutorAPI.getInstance().getPacketHandler().unregisterNetworkHandlers(PacketUtil.SIGN_BUS + 1);
        ExecutorAPI.getInstance().getPacketHandler().unregisterNetworkHandlers(PacketUtil.SIGN_BUS + 2);
        ExecutorAPI.getInstance().getPacketHandler().unregisterNetworkHandlers(PacketUtil.SIGN_BUS + 3);
    }

    @Nullable
    @Override
    public ApplicationUpdateRepository getUpdateRepository() {
        return REPOSITORY;
    }

    // ====

    public static ReformCloudApplication getInstance() {
        return instance;
    }

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

        Streams.filterToReference(signs, e -> e.getLocation().equals(cloudSign.getLocation())).ifPresent(signs::remove);
        insert(signs);
    }

    // ====

    private static Collection<CloudSign> read() {
        return ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().find(SignSystemAdapter.table, "signs", null, k -> k.get("signs", new TypeToken<Collection<CloudSign>>() {
        }));
    }

    private static void insert(Collection<CloudSign> signs) {
        ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().update(SignSystemAdapter.table, "signs", new JsonConfiguration().add("signs", signs));
    }
}
