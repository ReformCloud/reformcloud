/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.reformcloud.reformcloud2.executor.api.process.builder;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.process.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.task.Task;
import systems.reformcloud.reformcloud2.executor.api.wrappers.ProcessWrapper;

import java.util.Collection;
import java.util.UUID;

/**
 * A builder to construct processes
 */
public interface ProcessBuilder {

    /**
     * Sets the name of the target process factory in which the process information of the process
     * should get constructed. If the factory is not present in the node the fallback factory will
     * get used.
     *
     * @param targetProcessFactory The name of the process factory to construct the process information in
     * @return The same instance of this class as used to call the method
     */
    @NotNull
    ProcessBuilder targetProcessFactory(@Nullable String targetProcessFactory);

    /**
     * Sets the name of the process group to use to build the process group from. The group has to exist
     * if the name method is used otherwise {@link #prepare()} will fail throwing an exception.
     * <p>If {@link #group(ProcessGroup)} is used this method has no effect.</p>
     *
     * @param processGroupName The process group name to use as base of the new process
     * @return The same instance of this class as used to call the method
     */
    @NotNull
    ProcessBuilder group(@NotNull String processGroupName);

    /**
     * Sets the process group to prepare from. This process group has no need to be persistent.
     *
     * @param processGroup The process group to use as base for the new process
     * @return The same instance of this class as used to call the method
     */
    @NotNull
    ProcessBuilder group(@NotNull ProcessGroup processGroup);

    /**
     * Specifies the node on which the process should get started. If the node is not connected a
     * random other node will be used to start the process.
     *
     * @param node The name of the process which should start the process
     * @return The same instance of this class as used to call the method
     */
    @NotNull
    ProcessBuilder node(@NotNull String node);

    /**
     * Overrides the maximum amount of memory the process is allowed to use.
     *
     * @param memory The amount of memory the process is allowed to use (in MB)
     * @return The same instance of this class as used to call the method
     */
    @NotNull
    ProcessBuilder memory(int memory);

    /**
     * Sets the id of the current process. If the id already the next free id starting from the provided
     * id will get used
     *
     * @param id The id the process should use
     * @return The same instance of this class as used to call the method
     */
    @NotNull
    ProcessBuilder id(int id);

    /**
     * Sets the display name of the process to use
     *
     * @param displayName The display name to use for the process
     * @return The same instance of this class as used to call the method
     */
    @NotNull
    ProcessBuilder displayName(@NotNull String displayName);

    /**
     * Sets the message of the day (also known as MOTD) for the new process
     *
     * @param messageOfTheDay The message of the day which should get used for the process
     * @return The same instance of this class as used to call the method
     */
    @NotNull
    ProcessBuilder messageOfTheDay(@NotNull String messageOfTheDay);

    /**
     * Sets the maximum amount of players which are allowed to join the process
     *
     * @param maxPlayers The maximum amount of players which should be allowed to join
     * @return The same instance of this class as used to call the method
     */
    @NotNull
    ProcessBuilder maxPlayers(int maxPlayers);

    /**
     * Sets the template which should get used to start the process from
     *
     * @param template The template to start the process from
     * @return The same instance of this class as used to call the method
     */
    @NotNull
    ProcessBuilder template(@NotNull Template template);

    /**
     * Specifies the process inclusions which should get loaded before the start of the process
     *
     * @param inclusions The inclusions to load before the start of the process
     * @return The same instance of this class as used to call the method
     */
    @NotNull
    ProcessBuilder inclusions(@NonNls ProcessInclusion... inclusions);

    /**
     * Specifies the process inclusions which should get loaded before the start of the process
     *
     * @param inclusions The inclusions to load before the start of the process
     * @return The same instance of this class as used to call the method
     */
    @NotNull
    ProcessBuilder inclusions(@NotNull Collection<ProcessInclusion> inclusions);

    /**
     * Sets the extra data which will be available on the process for plugins
     *
     * @param extra The extra data
     * @return The same instance of this class as used to call the method
     */
    @NotNull
    ProcessBuilder extra(@NotNull JsonConfiguration extra);

    /**
     * Sets the initial process state which should get set after the connect of the process into the
     * network
     *
     * @param initialState The initial state
     * @return The same instance of this class as used to call the method
     */
    @NotNull
    ProcessBuilder initialState(@NotNull ProcessState initialState);

    /**
     * Sets the unique id which should get used for the process. If it's already taken a random one
     * will be used
     *
     * @param uniqueId The unique for the process
     * @return The same instance of this class as used to call the method
     */
    @NotNull
    ProcessBuilder uniqueId(@NotNull UUID uniqueId);

    /**
     * Prepares the process and returns the creates process wrapper if the creation was successful.
     *
     * @return A task completed with the created process or null if the process couldn't get created.
     */
    @NotNull
    Task<ProcessWrapper> prepare();
}
