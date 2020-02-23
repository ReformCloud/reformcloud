package systems.reformcloud.reformcloud2.placeholderapi.api;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReformCloudPlaceHolderExpansion extends PlaceholderExpansion {

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "rc";
    }

    @Override
    public String getAuthor() {
        return "ReformCloud";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Nullable
    @Override
    public String onPlaceholderRequest(@Nullable Player p, @Nonnull String params) {
        ProcessInformation current = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getThisProcessInformation();
        if (current == null) {
            return null;
        }

        switch (params) {
            case "current_online_count": {
                return Integer.toString(current.getOnlineCount());
            }

            case "current_max_players": {
                return Integer.toString(current.getMaxPlayers());
            }

            case "current_name": {
                return current.getName();
            }

            case "current_display_name": {
                return current.getDisplayName();
            }

            case "current_template": {
                return current.getTemplate().getName();
            }

            case "current_group": {
                return current.getProcessGroup().getName();
            }
        }

        return null;
    }
}
