package systems.reformcloud.reformcloud2.executor.api.common.process.detail;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.common.process.event.ProcessDetailConfigureEvent;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import java.util.UUID;

/**
 * Holds the details of a process like the unique id or name
 */
public final class ProcessDetail implements Nameable {

    /**
     * Creates a new process detail for a process
     *
     * @param processUniqueID The unique id of the process
     * @param parentUniqueID  The unique id of the startup picker which started the process
     * @param name            The name of the process
     * @param displayName     The display name of the process
     * @param id              The internal id of the process
     */
    @ApiStatus.Internal
    public ProcessDetail(@NotNull UUID processUniqueID, @NotNull UUID parentUniqueID, @NotNull String parentName,
                         @NotNull String name, @NotNull String displayName, int id, @NotNull Template template, int maxMemory) {
        this.processUniqueID = processUniqueID;
        this.parentUniqueID = parentUniqueID;
        this.parentName = parentName;
        this.name = name;
        this.displayName = displayName;
        this.id = id;
        this.template = template;
        this.maxMemory = maxMemory;
        this.creationTime = System.currentTimeMillis();

        this.processState = ProcessState.CREATED;
        this.processRuntimeInformation = ProcessRuntimeInformation.empty();

        ExecutorAPI.getInstance().getEventManager().callEvent(new ProcessDetailConfigureEvent(this));
    }

    private final UUID processUniqueID;

    private final UUID parentUniqueID;

    private final String parentName;

    private final String name;

    private final String displayName;

    private final int id;

    private final Template template;

    private final int maxMemory;

    private final long creationTime;

    private ProcessState processState;

    private int maxPlayers = -1;

    private String messageOfTheDay = "";

    private ProcessRuntimeInformation processRuntimeInformation;

    /**
     * @return The unique id of the process
     */
    @NotNull
    public UUID getProcessUniqueID() {
        return processUniqueID;
    }

    /**
     * @return The unique id of the startup picker (node/client)
     */
    @NotNull
    public UUID getParentUniqueID() {
        return parentUniqueID;
    }

    /**
     * @return The name of the startup picker which created the current detail
     */
    @NotNull
    public String getParentName() {
        return parentName;
    }

    /**
     * @return The name of the process
     */
    @NotNull
    @Override
    public String getName() {
        return name;
    }

    /**
     * @return The display name of the process
     */
    @NotNull
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return The id of the process
     */
    public int getId() {
        return id;
    }

    /**
     * @return The template on which the process is currently running
     */
    @NotNull
    public Template getTemplate() {
        return template;
    }

    /**
     * @return The max memory which is used for the current process
     */
    public int getMaxMemory() {
        return maxMemory == -1 ? this.template.getRuntimeConfiguration().getMaxMemory() : this.maxMemory;
    }

    /**
     * @return The milli time when this detail got created
     */
    public long getCreationTime() {
        return creationTime;
    }

    /**
     * @return The current process state of the process
     */
    @NotNull
    public ProcessState getProcessState() {
        return processState;
    }

    /**
     * @return The maximum amount of player or {@code -1} if no amount is specified
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * @return The message of the day of the current process
     */
    @NotNull
    public String getMessageOfTheDay() {
        return messageOfTheDay;
    }

    /**
     * @return The current runtime information about the process
     */
    @NotNull
    public ProcessRuntimeInformation getProcessRuntimeInformation() {
        return processRuntimeInformation;
    }

    /**
     * Sets the process state of the current process
     *
     * @param processState The new process state after the update
     */
    public void setProcessState(@NotNull ProcessState processState) {
        this.processState = processState;
    }

    /**
     * Sets the max players of the process. This will only affect the developers who use {@link #getMaxPlayers()}
     * and the placeholders for example in the signs. Also, the process state will update to full if
     * this count is reached.
     *
     * @param maxPlayers The new amount of max players which can join the process
     */
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    /**
     * Sets the message of the day for the current process
     *
     * @param messageOfTheDay The new message of the day which should get used
     */
    public void setMessageOfTheDay(@Nullable String messageOfTheDay) {
        this.messageOfTheDay = messageOfTheDay == null ? "" : messageOfTheDay;
    }

    /**
     * Sets the current runtime information to the given new one.
     *
     * @param processRuntimeInformation The new runtime information which should get used
     * @see ProcessRuntimeInformation#create()
     */
    public void setProcessRuntimeInformation(@NotNull ProcessRuntimeInformation processRuntimeInformation) {
        this.processRuntimeInformation = processRuntimeInformation;
    }
}
