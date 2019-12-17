package systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands;

import java.util.Arrays;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;

public final class CommandStop extends GlobalCommand {

  public CommandStop() {
    super("stop", "reformcloud.command.permissions", "The stop command",
          Arrays.asList("exit", "close", "end"));
  }

  @Override
  public boolean handleCommand(@Nonnull CommandSource commandSource,
                               @Nonnull String[] strings) {
    System.exit(0);
    return true;
  }
}
