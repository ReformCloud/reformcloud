package systems.reformcloud.reformcloud2.commands.config;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;

import java.util.List;

public class CommandsConfig implements SerializableObject {

    public CommandsConfig(boolean leaveCommandEnabled, List<String> leaveCommands, boolean reformCloudCommandEnabled, List<String> reformCloudCommands) {
        this.leaveCommandEnabled = leaveCommandEnabled;
        this.leaveCommands = leaveCommands;
        this.reformCloudCommandEnabled = reformCloudCommandEnabled;
        this.reformCloudCommands = reformCloudCommands;
    }

    private boolean leaveCommandEnabled;

    private List<String> leaveCommands;

    private boolean reformCloudCommandEnabled;

    private List<String> reformCloudCommands;

    public boolean isLeaveCommandEnabled() {
        return leaveCommandEnabled;
    }

    public List<String> getLeaveCommands() {
        return leaveCommands;
    }

    public boolean isReformCloudCommandEnabled() {
        return reformCloudCommandEnabled;
    }

    public List<String> getReformCloudCommands() {
        return reformCloudCommands;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeBoolean(this.leaveCommandEnabled);
        buffer.writeStringArray(this.leaveCommands);
        buffer.writeBoolean(this.reformCloudCommandEnabled);
        buffer.writeStringArray(this.reformCloudCommands);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.leaveCommandEnabled = buffer.readBoolean();
        this.leaveCommands = buffer.readStringArray();
        this.reformCloudCommandEnabled = buffer.readBoolean();
        this.reformCloudCommands = buffer.readStringArray();
    }
}
