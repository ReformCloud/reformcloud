package systems.reformcloud.reformcloud2.executor.api.common.process.running;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

/**
 * Represents a running process either in the node or client
 */
public interface RunningProcess {

    /**
     * Prepares the current process
     *
     * @return A task which will get completed when the complete process is prepared
     */
    @NotNull
    Task<Void> prepare();

    /**
     * Handles the queue of the process into the process queue of a node/client
     */
    void handleEnqueue();

    /**
     * Starts the current process
     *
     * @return If the process got started successfully
     */
    boolean bootstrap();

    /**
     * Closes the current process if it's running
     */
    void shutdown();

    /**
     * Copies the current process into the template
     */
    void copy();

    /**
     * @return An optional which is either empty if the process is not started or contains the current process
     */
    @NotNull
    ReferencedOptional<Process> getProcess();

    /**
     * @return The process information of the current service
     */
    @NotNull
    ProcessInformation getProcessInformation();

    /**
     * Sends the specified command to the console
     *
     * @param line The command line which should get sent
     */
    void sendCommand(@NotNull String line);

    /**
     * @return If the process is started an currently alive
     */
    boolean isAlive();

    /**
     * @return The startup time of the process or {@code -1} if the process is not started
     */
    long getStartupTime();

    /**
     * @return The path in which the process (should) operate
     */
    @NotNull
    Path getPath();

    /**
     * @return All shutdown commands for the current process
     */
    @NotNull
    default String[] getShutdownCommands() {
        Collection<String> commands = getProcessInformation().getProcessDetail().getTemplate().getRuntimeConfiguration().getShutdownCommands();
        commands.addAll(Arrays.asList("stop", "end"));
        return commands.stream().map(e -> e + "\n").toArray(String[]::new);
    }
}
