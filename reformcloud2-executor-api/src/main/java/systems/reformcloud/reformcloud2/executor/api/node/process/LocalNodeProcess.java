package systems.reformcloud.reformcloud2.executor.api.node.process;

import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;

import javax.annotation.Nonnull;

public interface LocalNodeProcess {

    long getStartupTime();

    void prepare();

    boolean bootstrap();

    void shutdown();

    void sendCommand(@Nonnull String line);

    boolean running();

    void copy();

    @Nonnull
    ReferencedOptional<Process> getProcess();

    @Nonnull
    ProcessInformation getProcessInformation();
}
