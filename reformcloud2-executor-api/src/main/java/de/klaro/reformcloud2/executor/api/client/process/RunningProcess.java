package de.klaro.reformcloud2.executor.api.client.process;

import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;

public interface RunningProcess {

    RunningProcess prepare();

    boolean bootstrap();

    boolean shutdown();

    boolean sendCommand(String command);

    boolean running();

    ProcessInformation getProcessInformation();
}
