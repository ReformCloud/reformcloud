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
package systems.reformcloud.reformcloud2.executor.node.config;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public final class NodeConfig {

    static final TypeToken<NodeConfig> TYPE = new TypeToken<NodeConfig>() {
    };

    static final Path PATH = Paths.get("reformcloud/config.json");

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
    }

    private final String name;

    private final UUID uniqueID;

    private final long maxMemory;

    private final String startHost;

    private List<NetworkAddress> networkListeners;

    private List<NetworkAddress> httpNetworkListeners;

    private List<NetworkAddress> clusterNodes;

    public String getName() {
        return name;
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    public String getStartHost() {
        return startHost;
    }

    public List<NetworkAddress> getNetworkListeners() {
        return networkListeners;
    }

    public List<NetworkAddress> getHttpNetworkListeners() {
        return httpNetworkListeners;
    }

    public List<NetworkAddress> getClusterNodes() {
        return clusterNodes;
    }

    @NotNull
    @Contract(" -> new")
    NodeInformation prepare() {
        return new NodeInformation(
                name,
                uniqueID,
                System.currentTimeMillis(),
                0L,
                maxMemory,
                new CopyOnWriteArrayList<>()
        );
    }

    public void save() {
        new JsonConfiguration().add("config", this).write(PATH);
    }

    public static class NetworkAddress {

        public NetworkAddress(String host, int port) {
            this.host = host;
            this.port = port;
        }

        private final String host;

        private final int port;

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }
    }
}
