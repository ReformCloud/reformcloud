package systems.reformcloud.reformcloud2.executor.api.common.groups.basic;

import java.util.List;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;

public final class DefaultMainGroup extends MainGroup {

  public DefaultMainGroup(String name, List<String> subGroups) {
    super(name, subGroups);
  }
}
