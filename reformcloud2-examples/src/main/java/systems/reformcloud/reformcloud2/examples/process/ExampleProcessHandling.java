/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.examples.process;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.process.api.ProcessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.process.api.ProcessConfigurationBuilder;
import systems.reformcloud.reformcloud2.executor.api.process.api.ProcessInclusion;
import systems.reformcloud.reformcloud2.executor.api.task.Task;

import java.util.UUID;

public class ExampleProcessHandling {

    // Starts a new process of the lobby group
    public static void startProcessFromGroup() {
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().startProcess("Lobby");
    }

    // Starts a new process of the group lobby with the template default
    public static void startProcessFromGroupWithTemplate() {
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().startProcess("Lobby", "default");
    }

    // Starts a new process based on the given process configuration
    public static void startNewProcessWithConfig() {
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().startProcess(createProcessConfiguration());
    }

    // Prepared a new process based on the given process configuration and starts it after the complete
    public static void prepareNewProcessWithConfigAndStartLater() {
        ProcessInformation information = ExecutorAPI.getInstance() // The information of the prepared process which is able to get started
                .getSyncAPI()
                .getProcessSyncAPI()
                .prepareProcess(createProcessConfiguration());
        if (information == null) {
            // Unable to process the request maybe no node is available to start the process
            return;
        }

        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().startProcess(information); // Starts the process based on the created information
    }

    @Nullable
    public static ProcessInformation getProcessByName(@NotNull String name) {
        return ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(name);
    }

    @NotNull
    public static Task<ProcessInformation> getProcessByNameAsync(@NotNull String name) {
        return ExecutorAPI.getInstance().getAsyncAPI().getProcessAsyncAPI().getProcessAsync(name);
    }

    public static void workWithGetProcessByNameAsync(@NotNull String name) {
        getProcessByNameAsync(name).onComplete(processInformation -> {
            if (processInformation == null) {
                // Unable to find process by the given name
                return;
            }

            // Work with the process information you have requested
            System.out.println("Unique id of process with name " + name + " is " + processInformation.getProcessDetail().getProcessUniqueID());
        }).onFailure(exception -> exception.printStackTrace());
    }

    @NotNull
    public static ProcessConfiguration createProcessConfiguration() {
        return ProcessConfigurationBuilder.newBuilder("Lobby") // Creates a new process config builder for the group lobby
                // Use the default template it's required for the template to be configured in
                // the group if you use only the name of the template
                .template("default")
                // Sets the extra information for the new process
                .extra(new JsonConfiguration().add("extra", "lol"))
                // Sets the display name of the new process
                .displayName("Lobby-" + UUID.randomUUID().toString())
                // Sets the internal process id, this is a start id (if the id is in use the id will count until the id is free)
                .id(5)
                // Adds a inclusion to the process, the name is the path on the process. It better if you use
                // a specific version number for better update handling
                .inclusion(new ProcessInclusion("https://dl.reformcloud.systems/addonsv2/reformcloud2-default-application-commands-2.2.0.jar", "plugins/perms.jar"))
                // The state the process will get after the connect in the network
                .initialState(ProcessState.INVISIBLE)
                // The port on which the process should run, this is a start port (if the port is in use the port will count until the port is free)
                .port(25566)
                // The unique id of the process
                .uniqueId(UUID.randomUUID())
                // The maximum amount of memory which should get used for the new process
                .maxMemory(512)
                // The maximum amount of players which is allowed to join the process
                .maxPlayers(1000)
                // Creates the process configuration
                .build();

    }
}
