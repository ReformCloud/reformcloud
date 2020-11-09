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
package systems.reformcloud.reformcloud2.executor.api.builder;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.groups.process.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.groups.process.player.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.process.startup.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.task.Task;

import java.util.List;

/**
 * A builder to construct process groups
 */
public interface ProcessGroupBuilder {

    /**
     * Sets the name of the process group to create
     *
     * @param name The name of the process group
     * @return The same instance of this class as used to call the method
     */
    @NotNull
    ProcessGroupBuilder name(@NotNull String name);

    /**
     * Sets weather the created process group should be a static one or not
     *
     * @param staticGroup If the process group should be static
     * @return The same instance of this class as used to call the method
     */
    @NotNull
    ProcessGroupBuilder staticGroup(boolean staticGroup);

    /**
     * Sets weather the created process group should be a lobby group or not. This is used to find out
     * if a server is a fallback.
     *
     * @param lobby If the process group should be a lobby group
     * @return The same instance of this class as used to call the method
     */
    @NotNull
    ProcessGroupBuilder lobby(boolean lobby);

    /**
     * Sets the templates of the process group. One template should be specified in order to start
     * a process without providing an explicit template.
     *
     * @param templates The templates which should be used for the process group
     * @return The same instance of this class as used to call the method
     */
    @NotNull
    ProcessGroupBuilder templates(@NonNls Template... templates);

    /**
     * Sets the templates of the process group. One template should be specified in order to start
     * a process without providing an explicit template.
     *
     * @param templates The templates which should be used for the process group
     * @return The same instance of this class as used to call the method
     */
    @NotNull
    ProcessGroupBuilder templates(@NotNull List<Template> templates);

    /**
     * Sets the player access configuration for the current process group
     *
     * @param configuration The player access configuration which should get used for the new group
     * @return The same instance of this class as used to call the method
     */
    @NotNull
    ProcessGroupBuilder playerAccessConfig(@NotNull PlayerAccessConfiguration configuration);

    /**
     * Sets the startup configuration for the current process group
     *
     * @param configuration The startup configuration used for some internal processes such as auto
     *                      process start
     * @return The same instance of this class as used to call the method
     */
    @NotNull
    ProcessGroupBuilder startupConfiguration(@NotNull StartupConfiguration configuration);

    /**
     * Sets weather the id of the group should be visible in a process based on this process group
     * display name.
     *
     * @param showId If the id should be visible in a process' name
     * @return The same instance of this class as used to call the method
     */
    @NotNull
    ProcessGroupBuilder showId(boolean showId);

    /**
     * Creates this process group persistently if it's not present already
     *
     * @return A task which is null after the completion if the group already exists, the created object otherwise
     */
    @NotNull
    Task<ProcessGroup> createPermanently();

    /**
     * Creates this process group without any checks just based on the given configuration. No need for
     * a deletion if the group is not used anymore. This may be helpful in testing environments or for
     * some internal systems.
     *
     * @return A process group object based on the current configuration
     */
    @NotNull
    ProcessGroup createTemporary();
}
