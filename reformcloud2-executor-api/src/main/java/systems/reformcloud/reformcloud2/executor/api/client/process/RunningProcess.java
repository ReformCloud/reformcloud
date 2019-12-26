package systems.reformcloud.reformcloud2.executor.api.client.process;

import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;

import java.util.Arrays;
import java.util.Collection;

public interface RunningProcess {

    long getStartupTime();

    RunningProcess prepare();

    boolean bootstrap();

    boolean shutdown();

    boolean sendCommand(String command);

    boolean running();

    void copy();

    ReferencedOptional<Process> getProcess();

    ProcessInformation getProcessInformation();

    default String[] getShutdownCommands() {
        Collection<String> commands = getProcessInformation().getTemplate().getRuntimeConfiguration().getShutdownCommands();
        commands.addAll(Arrays.asList("stop", "end"));
        return commands.stream().map(e -> e + "\n").toArray(String[]::new);
    }
}
