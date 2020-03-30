package systems.reformcloud.reformcloud2.signs.util;

import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import java.util.function.Function;

public final class PlaceHolderUtil {

    private PlaceHolderUtil() {
        throw new UnsupportedOperationException();
    }

    public static <T> T format(String line, String group, ProcessInformation processInformation, Function<String, T> function) {
        line = line.replace("%group%", group);
        line = line.replace("%name%", processInformation.getProcessDetail().getName());
        line = line.replace("%display%", processInformation.getProcessDetail().getDisplayName());
        line = line.replace("%parent%", processInformation.getProcessDetail().getParentName());
        line = line.replace("%id%", Integer.toString(processInformation.getProcessDetail().getId()));
        line = line.replace("%uniqueid%", processInformation.getProcessDetail().getProcessUniqueID().toString());
        line = line.replace("%state%", processInformation.getProcessDetail().getProcessState().name());
        line = line.replace("%connected%", Boolean.toString(processInformation.getNetworkInfo().isConnected()));
        line = line.replace("%template%", processInformation.getProcessDetail().getTemplate().getName());
        line = line.replace("%online%", Integer.toString(processInformation.getProcessPlayerManager().getOnlineCount()));
        line = line.replace("%max%", Integer.toString(processInformation.getProcessDetail().getMaxPlayers()));
        line = line.replace("%whitelist%",
                Boolean.toString(processInformation.getProcessGroup().getPlayerAccessConfiguration().isJoinOnlyPerPermission()));
        line = line.replace("%lobby%", Boolean.toString(processInformation.getProcessGroup().isCanBeUsedAsLobby()));
        line = line.replace("%static%", Boolean.toString(processInformation.getProcessGroup().isCanBeUsedAsLobby()));
        line = line.replace("%motd%", processInformation.getProcessDetail().getMessageOfTheDay());
        return function.apply(line);
    }
}
