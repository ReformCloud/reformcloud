package systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands;

import java.util.Collections;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.logger.LoggerBase;

public final class CommandClear extends GlobalCommand {

  public CommandClear(LoggerBase loggerBase) {
    super("clear", "reformcloud.command.clear", "Clears the console",
          Collections.emptyList());
    this.loggerBase = loggerBase;
  }

  private final LoggerBase loggerBase;

  @Override
  public boolean handleCommand(@Nonnull CommandSource commandSource,
                               @Nonnull String[] strings) {
    loggerBase.clearScreen();
    return true;
  }
}
