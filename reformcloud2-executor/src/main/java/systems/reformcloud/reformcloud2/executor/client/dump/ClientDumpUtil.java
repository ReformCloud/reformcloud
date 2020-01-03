package systems.reformcloud.reformcloud2.executor.client.dump;

import systems.reformcloud.reformcloud2.executor.api.client.process.RunningProcess;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.dump.DumpUtil;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.dump.basic.DefaultDumpUtil;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.client.ClientExecutor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;

public class ClientDumpUtil implements DumpUtil {

    private static final DumpUtil PARENT = new DefaultDumpUtil();

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat();

    @Override
    public void appendCurrentDump(StringBuilder stringBuilder) {
        PARENT.appendCurrentDump(stringBuilder);
        dumpProcessInfos(stringBuilder);
    }

    private static void dumpProcessInfos(StringBuilder stringBuilder) {
        Collection<RunningProcess> nodeProcesses = ClientExecutor.getInstance().getProcessManager().getAll();
        stringBuilder.append("--- Registered Client Processes (").append(nodeProcesses.size()).append(") ---");
        stringBuilder.append("\n");

        if (nodeProcesses.size() > 0) {
            nodeProcesses.forEach(e -> {
                ProcessInformation processInformation = e.getProcessInformation();
                stringBuilder
                        .append("Name: ")
                        .append(processInformation.getName())
                        .append("\n")
                        .append("UniqueID: ")
                        .append(processInformation.getProcessUniqueID())
                        .append("\n")
                        .append("Startup Time: ")
                        .append(e.getStartupTime() == -1 ? "unknown" : DATE_FORMAT.format(e.getStartupTime()));
                if (e.getStartupTime() != -1) {
                    stringBuilder
                            .append(" (")
                            .append(System.currentTimeMillis() - e.getStartupTime())
                            .append("ms (")
                            .append((System.currentTimeMillis() - e.getStartupTime()) / 60)
                            .append("s) ago");
                }

                stringBuilder
                        .append("\n")
                        .append("Running: ")
                        .append(e.running())
                        .append("\n\n");
            });
        } else {
            stringBuilder.append("No processes are registered on the current client").append("\n\n");
        }
    }
}
