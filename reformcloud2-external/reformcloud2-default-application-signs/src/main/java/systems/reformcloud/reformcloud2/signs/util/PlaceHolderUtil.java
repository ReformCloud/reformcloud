package systems.reformcloud.reformcloud2.signs.util;

import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import java.util.function.Function;

public final class PlaceHolderUtil {

    private PlaceHolderUtil() {
        throw new UnsupportedOperationException();
    }

    public static <T> T format(String line, String group, ProcessInformation processInformation, Function<String, T> function) {
        line = line.replace("%group%", group);
        line = line.replace("%name%", processInformation.getName());
        line = line.replace("%display%", processInformation.getDisplayName());
        line = line.replace("%parent%", processInformation.getParent());
        line = line.replace("%id%", Integer.toString(processInformation.getId()));
        line = line.replace("%uniqueid%", processInformation.getProcessUniqueID().toString());
        line = line.replace("%state%", processInformation.getProcessState().name());
        line = line.replace("%connected%", Boolean.toString(processInformation.getNetworkInfo().isConnected()));
        line = line.replace("%template%", processInformation.getTemplate().getName());
        line = line.replace("%online%", Integer.toString(processInformation.getOnlineCount()));
        line = line.replace("%max%", Integer.toString(processInformation.getMaxPlayers()));
        line = line.replace("%whitelist%",
                Boolean.toString(processInformation.getProcessGroup().getPlayerAccessConfiguration().isJoinOnlyPerPermission()));
        line = line.replace("%lobby%", Boolean.toString(processInformation.getProcessGroup().isCanBeUsedAsLobby()));
        line = line.replace("%static%", Boolean.toString(processInformation.getProcessGroup().isCanBeUsedAsLobby()));
        return function.apply(line);
    }
}
