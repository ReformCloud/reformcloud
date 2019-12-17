package systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands;

import java.util.Collections;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.utility.runtime.ReloadableRuntime;

public final class CommandReload extends GlobalCommand {

  public CommandReload(ReloadableRuntime reloadableRuntime) {
    super("reload", null, GlobalCommand.DEFAULT_DESCRIPTION,
          Collections.singletonList("rl"));

    this.reloadableRuntime = reloadableRuntime;
  }

  private final ReloadableRuntime reloadableRuntime;

  @Override
  public boolean handleCommand(@Nonnull CommandSource commandSource,
                               @Nonnull String[] strings) {
    try {
      reloadableRuntime.reload();
    } catch (final Exception ex) {
      ex.printStackTrace();
    }
    return true;
  }
}
