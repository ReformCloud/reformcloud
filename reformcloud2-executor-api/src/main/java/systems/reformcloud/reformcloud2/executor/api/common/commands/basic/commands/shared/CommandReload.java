package systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.shared;

import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.runtime.ReloadableRuntime;

import javax.annotation.Nonnull;
import java.util.Collections;

public final class CommandReload extends GlobalCommand {

    public CommandReload(ReloadableRuntime reloadableRuntime) {
        super("reload", null, GlobalCommand.DEFAULT_DESCRIPTION, Collections.singletonList(
                "rl"
        ));

        this.reloadableRuntime = reloadableRuntime;
    }

    private final ReloadableRuntime reloadableRuntime;

    @Override
    public void describeCommandToSender(@Nonnull CommandSource source) {
        source.sendMessage(LanguageManager.get("command-reload-description"));
    }

    @Override
    public boolean handleCommand(@Nonnull CommandSource commandSource, @Nonnull String[] strings) {
        try {
            reloadableRuntime.reload();
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }
}
