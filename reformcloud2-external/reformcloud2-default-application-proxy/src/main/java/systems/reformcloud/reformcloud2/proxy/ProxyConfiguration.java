package systems.reformcloud.reformcloud2.proxy;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.proxy.config.MotdConfiguration;
import systems.reformcloud.reformcloud2.proxy.config.TabListConfiguration;

import java.util.List;

public class ProxyConfiguration implements SerializableObject {

    public static final TypeToken<ProxyConfiguration> TYPE = new TypeToken<ProxyConfiguration>() {
    };

    public ProxyConfiguration() {
    }

    public ProxyConfiguration(List<MotdConfiguration> motdDefaultConfig, List<MotdConfiguration> motdMaintenanceConfig,
                              List<TabListConfiguration> tabListConfigurations) {
        this.motdDefaultConfig = motdDefaultConfig;
        this.motdMaintenanceConfig = motdMaintenanceConfig;
        this.tabListConfigurations = tabListConfigurations;
    }

    private List<MotdConfiguration> motdDefaultConfig;

    private List<MotdConfiguration> motdMaintenanceConfig;

    private List<TabListConfiguration> tabListConfigurations;

    public List<MotdConfiguration> getMotdDefaultConfig() {
        return motdDefaultConfig;
    }

    public List<MotdConfiguration> getMotdMaintenanceConfig() {
        return motdMaintenanceConfig;
    }

    public List<TabListConfiguration> getTabListConfigurations() {
        return tabListConfigurations;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeObjects(this.motdDefaultConfig);
        buffer.writeObjects(this.motdMaintenanceConfig);
        buffer.writeObjects(this.tabListConfigurations);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.motdDefaultConfig = buffer.readObjects(MotdConfiguration.class);
        this.motdMaintenanceConfig = buffer.readObjects(MotdConfiguration.class);
        this.tabListConfigurations = buffer.readObjects(TabListConfiguration.class);
    }
}
