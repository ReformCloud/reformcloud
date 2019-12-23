package systems.reformcloud.reformcloud2.proxy;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.proxy.config.MotdConfiguration;
import systems.reformcloud.reformcloud2.proxy.config.TabListConfiguration;

import java.util.List;

public class ProxyConfiguration {

    public static final TypeToken<ProxyConfiguration> TYPE = new TypeToken<ProxyConfiguration>() {};

    public ProxyConfiguration(List<MotdConfiguration> motdDefaultConfig, List<MotdConfiguration> motdMaintenanceConfig,
                              List<TabListConfiguration> tabListConfigurations) {
        this.motdDefaultConfig = motdDefaultConfig;
        this.motdMaintenanceConfig = motdMaintenanceConfig;
        this.tabListConfigurations = tabListConfigurations;
    }

    private final List<MotdConfiguration> motdDefaultConfig;

    private final List<MotdConfiguration> motdMaintenanceConfig;

    private final List<TabListConfiguration> tabListConfigurations;

    public List<MotdConfiguration> getMotdDefaultConfig() {
        return motdDefaultConfig;
    }

    public List<MotdConfiguration> getMotdMaintenanceConfig() {
        return motdMaintenanceConfig;
    }

    public List<TabListConfiguration> getTabListConfigurations() {
        return tabListConfigurations;
    }
}
