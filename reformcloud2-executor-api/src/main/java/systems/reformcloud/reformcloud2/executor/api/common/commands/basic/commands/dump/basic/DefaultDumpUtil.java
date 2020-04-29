package systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.dump.basic;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.dump.DumpUtil;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.Player;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultDumpUtil implements DumpUtil {

    private final DateFormat dateFormat = new SimpleDateFormat();

    @Override
    public void appendCurrentDump(StringBuilder stringBuilder) {
        bumpMainGroups(stringBuilder);
        bumpProcessGroups(stringBuilder);
        bumpStartedProcesses(stringBuilder);
        bumpNetwork(stringBuilder);
    }

    private void bumpNetwork(StringBuilder stringBuilder) {
        List<PacketSender> allSender = DefaultChannelManager.INSTANCE.getAllSender();
        stringBuilder.append("--- Connected Channels (").append(allSender.size()).append(") ---");
        stringBuilder.append("\n");

        if (allSender.size() > 0) {
            allSender.forEach(e -> {
                long connectionTime = System.currentTimeMillis() - e.getConnectionTime();
                stringBuilder
                        .append("Name: ")
                        .append(e.getName())
                        .append("\n")
                        .append("Connected Since: ")
                        .append(dateFormat.format(e.getConnectionTime()))
                        .append(" (")
                        .append(connectionTime)
                        .append("ms / ")
                        .append(connectionTime / 60)
                        .append("s)")
                        .append("\n")
                        .append("Connected: ")
                        .append(e.isConnected())
                        .append("\n\n");
            });
        } else {
            stringBuilder.append("No channels are known").append("\n\n");
        }
    }

    private void bumpMainGroups(StringBuilder stringBuilder) {
        List<MainGroup> mainGroups = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getMainGroups();
        stringBuilder.append("--- Registered Main Groups (").append(mainGroups.size()).append(") ---");
        stringBuilder.append("\n");

        if (mainGroups.size() > 0) {
            mainGroups.forEach(g ->
                    stringBuilder
                            .append("Name: ")
                            .append(g.getName())
                            .append("\n")
                            .append("SubGroups: ")
                            .append(String.format("%d total, %s", g.getSubGroups().size(), String.join(" ", g.getSubGroups())))
                            .append("\n\n")
            );
        } else {
            stringBuilder.append("No main groups are registered").append("\n\n");
        }
    }

    private void bumpProcessGroups(StringBuilder stringBuilder) {
        List<ProcessGroup> processGroups = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroups();
        stringBuilder.append("--- Registered Sub Groups (").append(processGroups.size()).append(") ---");
        stringBuilder.append("\n");

        if (processGroups.size() > 0) {
            processGroups.forEach(e -> {
                Collection<String> templates = e.getTemplates().stream().map(t -> {
                    Collection<String> env = t.getRuntimeConfiguration().getSystemProperties().entrySet()
                            .stream().map(u -> u.getKey() + "=" + u.getValue()).collect(Collectors.toList());

                    return "Name: " + t.getName()
                            + "\n   Version: " + t.getVersion().name()
                            + "\n   Global: " + t.isGlobal()
                            + "\n   System-Properties: "
                            + String.format("%d total, %s", env.size(), String.join(" ", env))
                            + "\n   Parameters: "
                            + String.format("%d total, %s", t.getRuntimeConfiguration().getProcessParameters().size(), String.join(" ", t.getRuntimeConfiguration().getProcessParameters()));
                }).collect(Collectors.toList());

                stringBuilder
                        .append("Name: ")
                        .append(e.getName())
                        .append("\n")
                        .append("Maintenance: ")
                        .append(e.getPlayerAccessConfiguration().isMaintenance())
                        .append(" / Join-per-permission: ")
                        .append(e.getPlayerAccessConfiguration().isJoinOnlyPerPermission())
                        .append(" / Cloud player limit: ")
                        .append(e.getPlayerAccessConfiguration().isUseCloudPlayerLimit())
                        .append("\n")
                        .append("Templates: ")
                        .append(String.format("%d total, %s", e.getTemplates().size(), String.join(" ", templates)))
                        .append("\n\n");
            });
        } else {
            stringBuilder.append("No sub groups are registered").append("\n\n");
        }
    }

    private void bumpStartedProcesses(StringBuilder stringBuilder) {
        List<ProcessInformation> allProcesses = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getAllProcesses();
        stringBuilder.append("--- Started Processes (").append(allProcesses.size()).append(") ---");
        stringBuilder.append("\n");

        if (allProcesses.size() > 0) {
            allProcesses.forEach(e -> {
                Collection<String> players = e.getProcessPlayerManager().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
                Collection<String> plugins = e.getPlugins().stream().map(
                        p -> p.getName() + " of " + p.author() + " (V" + p.version() + ")"
                ).collect(Collectors.toList());

                stringBuilder
                        .append("Name: ")
                        .append(e.getProcessDetail().getName())
                        .append(" (Display Name: ")
                        .append(e.getProcessDetail().getDisplayName())
                        .append(") ")
                        .append("\n")
                        .append("Online: ")
                        .append(String.format("%d total, %s", players.size(), String.join(" ", players)))
                        .append("\n")
                        .append("Plugins: ")
                        .append(String.format("%d total, %s", plugins.size(), String.join(" ", plugins)))
                        .append("\n")
                        .append("Template: ")
                        .append("\n")
                        .append("   Name: ")
                        .append(e.getProcessDetail().getTemplate().getName())
                        .append("\n")
                        .append("   Backend: ")
                        .append(e.getProcessDetail().getTemplate().getBackend())
                        .append("\n")
                        .append("   Version: ")
                        .append(e.getProcessDetail().getTemplate().getVersion().name())
                        .append("\n\n");
            });
        } else {
            stringBuilder.append("No processes are started").append("\n\n");
        }
    }
}

