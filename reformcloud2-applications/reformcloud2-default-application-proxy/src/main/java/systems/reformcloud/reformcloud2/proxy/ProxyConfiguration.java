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
    private List<MotdConfiguration> motdDefaultConfig;
    private List<MotdConfiguration> motdMaintenanceConfig;
    private List<TabListConfiguration> tabListConfigurations;

    public ProxyConfiguration() {
    }

    public ProxyConfiguration(List<MotdConfiguration> motdDefaultConfig, List<MotdConfiguration> motdMaintenanceConfig,
                              List<TabListConfiguration> tabListConfigurations) {
        this.motdDefaultConfig = motdDefaultConfig;
        this.motdMaintenanceConfig = motdMaintenanceConfig;
        this.tabListConfigurations = tabListConfigurations;
    }

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
