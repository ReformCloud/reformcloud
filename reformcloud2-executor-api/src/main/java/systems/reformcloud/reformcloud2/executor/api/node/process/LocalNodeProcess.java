package systems.reformcloud.reformcloud2.executor.api.node.process;

import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface LocalNodeProcess {

    long getStartupTime();

    void prepare();

    boolean bootstrap();

    void shutdown();

    void sendCommand(@Nonnull String line);

    boolean running();

    void copy();

    CompletableFuture<Void> initTemplate();

    @Nonnull
    ReferencedOptional<Process> getProcess();

    @Nonnull
    ProcessInformation getProcessInformation();

    default String[] getShutdownCommands() {
        Collection<String> commands = getProcessInformation().getTemplate().getRuntimeConfiguration().getShutdownCommands();
        commands.addAll(Arrays.asList("stop", "end"));
        return commands.stream().map(e -> e + "\n").toArray(String[]::new);
    }
}
