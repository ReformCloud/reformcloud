package systems.reformcloud.reformcloud2.commands.config;

import java.util.List;

public class CommandsConfig {

  public CommandsConfig(boolean leaveCommandEnabled, List<String> leaveCommands,
                        boolean reformCloudCommandEnabled,
                        List<String> reformCloudCommands) {
    this.leaveCommandEnabled = leaveCommandEnabled;
    this.leaveCommands = leaveCommands;
    this.reformCloudCommandEnabled = reformCloudCommandEnabled;
    this.reformCloudCommands = reformCloudCommands;
  }

  private final boolean leaveCommandEnabled;

  private final List<String> leaveCommands;

  private final boolean reformCloudCommandEnabled;

  private final List<String> reformCloudCommands;

  public boolean isLeaveCommandEnabled() { return leaveCommandEnabled; }

  public List<String> getLeaveCommands() { return leaveCommands; }

  public boolean isReformCloudCommandEnabled() {
    return reformCloudCommandEnabled;
  }

  public List<String> getReformCloudCommands() { return reformCloudCommands; }
}
