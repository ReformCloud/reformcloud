/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
import systems.reformcloud.reformcloud2.signs.application.packets.PacketCreateSign;
import systems.reformcloud.reformcloud2.signs.application.packets.PacketDeleteBulkSigns;
import systems.reformcloud.reformcloud2.signs.application.packets.PacketDeleteSign;
import systems.reformcloud.reformcloud2.signs.application.packets.PacketRequestSignLayouts;
import systems.reformcloud.reformcloud2.signs.application.updater.SignsUpdater;
import systems.reformcloud.reformcloud2.signs.packets.PacketReloadSignConfig;
import systems.reformcloud.reformcloud2.signs.util.SignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignConfig;

import java.util.Collection;
import java.util.Collections;

public class ReformCloudApplication extends Application {

    private static final ApplicationUpdateRepository REPOSITORY = new SignsUpdater();
    private static SignConfig signConfig;
    private static JsonConfiguration databaseEntry;
    private static ReformCloudApplication instance;

    public static ReformCloudApplication getInstance() {
        return instance;
    }

    public static SignConfig getSignConfig() {
        return signConfig;
    }

    public static void insert(CloudSign cloudSign) {
        Collection<CloudSign> signs = read();
        if (signs == null) {
            return;
        }

        signs.add(cloudSign);
        databaseEntry.add("signs", signs);

        insert();
    }

    public static void delete(CloudSign cloudSign) {
        Collection<CloudSign> signs = read();
        if (signs == null) {
            return;
        }

        Streams.filterToReference(signs, e -> e.getLocation().equals(cloudSign.getLocation())).ifPresent(signs::remove);
        databaseEntry.add("signs", signs);

        insert();
    }

    // ====

    private static Collection<CloudSign> read() {
        return databaseEntry.get("signs", new TypeToken<Collection<CloudSign>>() {
        });
    }

    private static void insert() {
        ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().update(SignSystemAdapter.table, "signs", databaseEntry);
    }

    // ====

    @Override
    public void onInstallable() {
        ExecutorAPI.getInstance().getEventManager().registerListener(new ProcessInclusionHandler());
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    // ====

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
                PacketCreateSign.class,
                PacketDeleteSign.class,
                PacketDeleteBulkSigns.class,
                PacketRequestSignLayouts.class
        );

        databaseEntry = ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().find(SignSystemAdapter.table, "signs", null);
        signConfig = ConfigHelper.read(dataFolder().getPath());
        DefaultChannelManager.INSTANCE.getAllSender().forEach(e -> e.sendPacket(new PacketReloadSignConfig(signConfig)));
    }

    @Nullable
    @Override
    public ApplicationUpdateRepository getUpdateRepository() {
        return REPOSITORY;
    }
}
