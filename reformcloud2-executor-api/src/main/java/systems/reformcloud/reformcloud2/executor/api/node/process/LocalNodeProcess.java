package systems.reformcloud.reformcloud2.executor.api.node.process;

import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;

public interface LocalNodeProcess {

    long getStartupTime();

    void prepare();

    boolean bootstrap();

    void shutdown();

    void sendCommand(String line);

    boolean running();

    void copy();

    ReferencedOptional<Process> getProcess();

    ProcessInformation getProcessInformation();
}
