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
package systems.reformcloud.reformcloud2.executor.api.common.plugins;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import java.util.List;

/**
 * This class represents any loaded plugin on a process instance
 *
 * @see ProcessInformation#getPlugins()
 */
public abstract class Plugin implements Nameable, SerializableObject {

    public static final TypeToken<DefaultPlugin> TYPE = new TypeToken<DefaultPlugin>() {
    };

    /**
     * @return The version of the plugin
     */
    @NotNull
    public abstract String version();

    /**
     * @return The author of the plugin
     */
    @Nullable
    public abstract String author();

    /**
     * @return The main class of the plugin
     */
    @NotNull
    public abstract String main();

    /**
     * @return The depends of the plugin
     */
    @Nullable
    public abstract List<String> depends();

    /**
     * @return The softpends of the plugin
     */
    @Nullable
    public abstract List<String> softpends();

    /**
     * @return If the plugin is enabled
     */
    public abstract boolean enabled();
}
