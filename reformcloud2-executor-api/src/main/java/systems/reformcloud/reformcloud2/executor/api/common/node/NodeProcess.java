package systems.reformcloud.reformcloud2.executor.api.common.node;

import java.util.UUID;

public class NodeProcess {

  private String group;

  private String name;

  private UUID uniqueID;

  public NodeProcess(String group, String name, UUID uniqueID) {
    this.group = group;
    this.name = name;
    this.uniqueID = uniqueID;
  }

  public String getGroup() { return group; }

  public String getName() { return name; }

  public UUID getUniqueID() { return uniqueID; }
}
