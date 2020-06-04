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
package systems.reformcloud.reformcloud2.node.config;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.utility.NetworkAddress;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

public final class NodeConfig {

    static final TypeToken<NodeConfig> TYPE = new TypeToken<NodeConfig>() {
    };

    static final Path PATH = Paths.get("reformcloud/config.json");
    private final String name;
    private final UUID uniqueID;
    private final long maxMemory;
    private final String startHost;
    private final double maxSystemCpuUsage;
    private final List<NetworkAddress> networkListeners;
    private final List<NetworkAddress> httpNetworkListeners;
    private final List<NetworkAddress> clusterNodes;

    public NodeConfig(String name, UUID uniqueID, long maxMemory, String startHost,
                      List<NetworkAddress> networkListeners, List<NetworkAddress> httpNetworkListeners,
                      List<NetworkAddress> clusterNodes) {
        this.name = name;
        this.uniqueID = uniqueID;
        this.maxMemory = maxMemory;
        this.startHost = startHost;
        this.networkListeners = networkListeners;
        this.httpNetworkListeners = httpNetworkListeners;
        this.clusterNodes = clusterNodes;
        this.maxSystemCpuUsage = 90D;
    }

    public String getName() {
        return this.name;
    }

    public UUID getUniqueID() {
        return this.uniqueID;
    }

    public long getMaxMemory() {
        return this.maxMemory;
    }

    public String getStartHost() {
        return this.startHost;
    }

    public double getMaxSystemCpuUsage() {
        return this.maxSystemCpuUsage == 0 ? 90D : this.maxSystemCpuUsage;
    }

    public List<NetworkAddress> getNetworkListeners() {
        return this.networkListeners;
    }

    public List<NetworkAddress> getHttpNetworkListeners() {
        return this.httpNetworkListeners;
    }

    public List<NetworkAddress> getClusterNodes() {
        return this.clusterNodes;
    }

    public void save() {
        new JsonConfiguration().add("config", this).write(PATH);
    }
}
