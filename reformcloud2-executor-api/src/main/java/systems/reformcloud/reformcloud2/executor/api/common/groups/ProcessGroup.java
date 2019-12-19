package systems.reformcloud.reformcloud2.executor.api.common.groups;

import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

public class ProcessGroup implements Nameable {

  public static final TypeToken<ProcessGroup> TYPE =
      new TypeToken<ProcessGroup>() {};

  public ProcessGroup(String name, boolean showIdInName,
                      StartupConfiguration startupConfiguration,
                      List<Template> templates,
                      PlayerAccessConfiguration playerAccessConfiguration,
                      boolean staticProcess) {
    this.name = name;
    this.showIdInName = showIdInName;
    this.startupConfiguration = startupConfiguration;
    this.templates = templates;
    this.playerAccessConfiguration = playerAccessConfiguration;
    this.staticProcess = staticProcess;
    this.canBeUsedAsLobby = false;
  }

  public ProcessGroup(String name, boolean showIdInName,
                      StartupConfiguration startupConfiguration,
                      List<Template> templates,
                      PlayerAccessConfiguration playerAccessConfiguration,
                      boolean staticProcess, boolean asLobby) {
    this.name = name;
    this.showIdInName = showIdInName;
    this.startupConfiguration = startupConfiguration;
    this.templates = templates;
    this.playerAccessConfiguration = playerAccessConfiguration;
    this.staticProcess = staticProcess;
    this.canBeUsedAsLobby = asLobby;
  }

  private String name;

  private boolean showIdInName;

  private StartupConfiguration startupConfiguration;

  private List<Template> templates;

  private PlayerAccessConfiguration playerAccessConfiguration;

  private boolean staticProcess;

  private boolean canBeUsedAsLobby;

  public boolean isShowIdInName() { return showIdInName; }

  @Nonnull
  public StartupConfiguration getStartupConfiguration() {
    return startupConfiguration;
  }

  @Nonnull
  public List<Template> getTemplates() {
    return templates;
  }

  @Nonnull
  public PlayerAccessConfiguration getPlayerAccessConfiguration() {
    return playerAccessConfiguration;
  }

  public boolean isStaticProcess() { return staticProcess; }

  public boolean isCanBeUsedAsLobby() { return canBeUsedAsLobby; }

  @Nonnull
  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof ProcessGroup))
      return false;
    ProcessGroup that = (ProcessGroup)o;
    return isShowIdInName() == that.isShowIdInName() &&
        isStaticProcess() == that.isStaticProcess() &&
        isCanBeUsedAsLobby() == that.isCanBeUsedAsLobby() &&
        Objects.equals(getName(), that.getName()) &&
        Objects.equals(getStartupConfiguration(),
                       that.getStartupConfiguration()) &&
        Objects.equals(getTemplates(), that.getTemplates()) &&
        Objects.equals(getPlayerAccessConfiguration(),
                       that.getPlayerAccessConfiguration());
  }
}
