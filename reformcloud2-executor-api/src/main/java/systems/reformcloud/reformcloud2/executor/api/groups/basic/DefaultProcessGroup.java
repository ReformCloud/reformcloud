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
package systems.reformcloud.reformcloud2.executor.api.groups.basic;

import systems.reformcloud.reformcloud2.executor.api.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.template.RuntimeConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.groups.template.backend.basic.FileTemplateBackend;
import systems.reformcloud.reformcloud2.executor.api.groups.template.inclusion.Inclusion;
import systems.reformcloud.reformcloud2.executor.api.groups.utils.AutomaticStartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.utils.StartupConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public final class DefaultProcessGroup extends ProcessGroup {

    public static final Inclusion PROXY_INCLUSION = new Inclusion(
        "reformcloud/global/proxies",
        FileTemplateBackend.NAME,
        Inclusion.InclusionLoadType.PAST
    );

    public static final Inclusion SERVER_INCLUSION = new Inclusion(
        "reformcloud/global/servers",
        FileTemplateBackend.NAME,
        Inclusion.InclusionLoadType.PAST
    );

    public DefaultProcessGroup(String name, int port, Version version, int maxMemory, boolean maintenance, int maxPlayers) {
        this(name, port, version, maxMemory, maintenance, maxPlayers, false, true);
    }

    public DefaultProcessGroup(String name, int port, Version version,
                               int maxMemory, boolean maintenance, int maxPlayers, boolean staticServer, boolean lobby) {
        super(
            name,
            true,
            new StartupConfiguration(
                -1,
                1,
                port,
                "java",
                true,
                new ArrayList<>()
            ), new ArrayList<>(Collections.singletonList(new Template(
                0,
                "default",
                false,
                FileTemplateBackend.NAME,
                "-",
                new RuntimeConfiguration(
                    maxMemory,
                    new ArrayList<>(),
                    new HashMap<>(Collections.singletonMap("reformcloud2.developer", "derklaro"))
                ),
                version,
                new ArrayList<>(),
                new ArrayList<>(Collections.singletonList(version.isServer() ? SERVER_INCLUSION : PROXY_INCLUSION))
            ))), new PlayerAccessConfiguration(
                "reformcloud.join.full",
                maintenance,
                "reformcloud.join.maintenance",
                false,
                "reformcloud.join",
                true,
                maxPlayers
            ), staticServer, lobby);
    }

    public DefaultProcessGroup(String name, int port, Version version,
                               int maxMemory, boolean maintenance, int min, int max, boolean staticServer, boolean lobby) {
        super(
            name,
            true,
            new StartupConfiguration(
                max,
                min,
                port,
                "java",
                true,
                new ArrayList<>()
            ), new ArrayList<>(Collections.singletonList(new Template(
                0,
                "default",
                false,
                FileTemplateBackend.NAME,
                "-",
                new RuntimeConfiguration(
                    maxMemory,
                    new ArrayList<>(),
                    new HashMap<>(Collections.singletonMap("reformcloud2.developer", "derklaro"))
                ),
                version,
                new ArrayList<>(),
                new ArrayList<>(Collections.singletonList(version.isServer() ? SERVER_INCLUSION : PROXY_INCLUSION))
            ))), new PlayerAccessConfiguration(
                "reformcloud.join.full",
                maintenance,
                "reformcloud.join.maintenance",
                false,
                "reformcloud.join",
                true,
                50
            ), staticServer, lobby);
    }

    public DefaultProcessGroup(String name, int port, Version version,
                               int maxMemory, boolean maintenance, int min, int max, int prepared, int priority,
                               boolean staticServer, boolean lobby, List<String> nodes, int maxPlayers) {
        super(
            name,
            true,
            new StartupConfiguration(
                max,
                min,
                prepared,
                port,
                "java",
                AutomaticStartupConfiguration.defaults(),
                nodes.isEmpty(),
                nodes
            ), new ArrayList<>(Collections.singletonList(new Template(
                0,
                "default",
                false,
                FileTemplateBackend.NAME,
                "-",
                new RuntimeConfiguration(
                    maxMemory,
                    Collections.emptyList(),
                    Collections.singletonMap("reformcloud2.developer", "derklaro")
                ),
                version,
                new ArrayList<>(),
                Collections.singletonList(version.isServer() ? SERVER_INCLUSION : PROXY_INCLUSION)
            ))), new PlayerAccessConfiguration(
                "reformcloud.join.full",
                maintenance,
                "reformcloud.join.maintenance",
                false,
                "reformcloud.join",
                true,
                maxPlayers
            ), staticServer, lobby);
    }
}
