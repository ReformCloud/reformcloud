package de.klaro.reformcloud2.executor.api.client.process;

import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;

import java.util.Optional;

public interface RunningProcess {

    long getStartupTime();

    RunningProcess prepare();

    boolean bootstrap();

    boolean shutdown();

    boolean sendCommand(String command);

    boolean running();

    void copy();

    Optional<Process> getProcess();

    ProcessInformation getProcessInformation();
}
