package systems.reformcloud.reformcloud2.executor.api.common.commands.basic.command.sources;

import java.util.List;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.AllowedCommandSources;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;

public abstract class RestAPICommand extends GlobalCommand {

  public RestAPICommand(String command, String permission, String description,
                        List<String> aliases) {
    super(command, permission, description, aliases);
  }

  public RestAPICommand(String command, String description,
                        List<String> aliases) {
    super(command, description, aliases);
  }

  public RestAPICommand(String command, String description) {
    super(command, description);
  }

  public RestAPICommand(String command) { super(command); }

  @Nonnull
  @Override
  public AllowedCommandSources sources() {
    return AllowedCommandSources.REST;
  }
}
