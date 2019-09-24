package de.klaro.reformcloud2.executor.api.client.process;

import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import de.klaro.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;

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
}
