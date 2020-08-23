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
package systems.reformcloud.reformcloud2.executor.api.groups.utils;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class StartupConfiguration implements SerializableObject {

    public static StartupConfiguration createDefault() {
        return new StartupConfiguration(
                -1,
                1,
                0,
                "java",
                true,
                Collections.emptyList()
        );
    }

    private int maxOnlineProcesses;
    private int minOnlineProcesses;
    private int alwaysPreparedProcesses;
    private int startPort;
    private String jvmCommand;
    private AutomaticStartupConfiguration automaticStartupConfiguration;
    private boolean searchBestClientAlone;
    private List<String> useOnlyTheseClients;

    @ApiStatus.Internal
    public StartupConfiguration() {
    }

    public StartupConfiguration(int maxOnlineProcesses, int minOnlineProcesses,
                                int startPort, String jvmCommand,
                                boolean searchBestClientAlone, List<String> useOnlyTheseClients) {
        this(maxOnlineProcesses, minOnlineProcesses, startPort, jvmCommand,
                AutomaticStartupConfiguration.defaults(), searchBestClientAlone, useOnlyTheseClients);
    }

    public StartupConfiguration(int maxOnlineProcesses, int minOnlineProcesses, int startPort, String jvmCommand,
                                AutomaticStartupConfiguration automaticStartupConfiguration,
                                boolean searchBestClientAlone, List<String> useOnlyTheseClients) {
        this(maxOnlineProcesses, minOnlineProcesses, 1, startPort, jvmCommand,
                automaticStartupConfiguration, searchBestClientAlone, useOnlyTheseClients);
    }

    public StartupConfiguration(int maxOnlineProcesses, int minOnlineProcesses, int alwaysPreparedProcesses,
                                int startPort, String jvmCommand, AutomaticStartupConfiguration automaticStartupConfiguration,
                                boolean searchBestClientAlone, List<String> useOnlyTheseClients) {
        this.maxOnlineProcesses = maxOnlineProcesses;
        this.minOnlineProcesses = minOnlineProcesses;
        this.alwaysPreparedProcesses = alwaysPreparedProcesses;
        this.startPort = startPort;
        this.jvmCommand = jvmCommand;
        this.automaticStartupConfiguration = automaticStartupConfiguration;
        this.searchBestClientAlone = searchBestClientAlone;
        this.useOnlyTheseClients = useOnlyTheseClients;
    }

    public int getMaxOnlineProcesses() {
        return this.maxOnlineProcesses;
    }

    public void setMaxOnlineProcesses(int maxOnlineProcesses) {
        this.maxOnlineProcesses = maxOnlineProcesses;
    }

    public int getMinOnlineProcesses() {
        return this.minOnlineProcesses;
    }

    public void setMinOnlineProcesses(int minOnlineProcesses) {
        this.minOnlineProcesses = minOnlineProcesses;
    }

    public int getAlwaysPreparedProcesses() {
        return this.alwaysPreparedProcesses;
    }

    public void setAlwaysPreparedProcesses(int alwaysPreparedProcesses) {
        this.alwaysPreparedProcesses = alwaysPreparedProcesses;
    }

    public int getStartPort() {
        return this.startPort;
    }

    public void setStartPort(int startPort) {
        this.startPort = startPort;
    }

    public String getJvmCommand() {
        return this.jvmCommand == null ? "java" : this.jvmCommand;
    }

    @NotNull
    public AutomaticStartupConfiguration getAutomaticStartupConfiguration() {
        return this.automaticStartupConfiguration;
    }

    public boolean isSearchBestClientAlone() {
        return this.searchBestClientAlone;
    }

    @NotNull
    public List<String> getUseOnlyTheseClients() {
        return this.useOnlyTheseClients;
    }

    public void setUseOnlyTheseClients(List<String> useOnlyTheseClients) {
        this.useOnlyTheseClients = useOnlyTheseClients;
        this.searchBestClientAlone = useOnlyTheseClients.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StartupConfiguration)) return false;
        StartupConfiguration that = (StartupConfiguration) o;
        return this.getMaxOnlineProcesses() == that.getMaxOnlineProcesses() &&
                this.getMinOnlineProcesses() == that.getMinOnlineProcesses() &&
                this.getStartPort() == that.getStartPort() &&
                this.isSearchBestClientAlone() == that.isSearchBestClientAlone() &&
                this.getJvmCommand().equals(that.getJvmCommand()) &&
                Objects.equals(this.getUseOnlyTheseClients(), that.getUseOnlyTheseClients());
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeInt(this.maxOnlineProcesses);
        buffer.writeInt(this.minOnlineProcesses);
        buffer.writeInt(this.alwaysPreparedProcesses);
        buffer.writeInt(this.startPort);
        buffer.writeString(this.jvmCommand);
        buffer.writeObject(this.automaticStartupConfiguration);
        buffer.writeBoolean(this.searchBestClientAlone);
        buffer.writeStringArray(this.useOnlyTheseClients);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.maxOnlineProcesses = buffer.readInt();
        this.minOnlineProcesses = buffer.readInt();
        this.alwaysPreparedProcesses = buffer.readInt();
        this.startPort = buffer.readInt();
        this.jvmCommand = buffer.readString();
        this.automaticStartupConfiguration = buffer.readObject(AutomaticStartupConfiguration.class);
        this.searchBestClientAlone = buffer.readBoolean();
        this.useOnlyTheseClients = buffer.readStringArray();
    }
}
