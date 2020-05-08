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
package systems.reformcloud.reformcloud2.executor.api.common.plugins.basic;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.Plugin;

import java.util.List;

public final class DefaultPlugin extends Plugin {

    @ApiStatus.Internal
    public DefaultPlugin() {
    }

    public DefaultPlugin(String version, String author, String main, List<String> depends, List<String> softpends, boolean enabled, String name) {
        this.version = version;
        this.author = author;
        this.main = main;
        this.depends = depends;
        this.softpends = softpends;
        this.enabled = enabled;
        this.name = name;
    }

    private String version;

    private String author;

    private String main;

    private List<String> depends;

    private List<String> softpends;

    private boolean enabled;

    private String name;

    @NotNull
    @Override
    public String version() {
        return version;
    }

    @Nullable
    @Override
    public String author() {
        return author;
    }

    @NotNull
    @Override
    public String main() {
        return main;
    }

    @Override
    public List<String> depends() {
        return depends;
    }

    @Override
    public List<String> softpends() {
        return softpends;
    }

    @Override
    public boolean enabled() {
        return enabled;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.version);
        buffer.writeString(this.author);
        buffer.writeString(this.main);
        buffer.writeStringArray(this.depends);
        buffer.writeStringArray(this.softpends);
        buffer.writeBoolean(this.enabled);
        buffer.writeString(this.name);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.version = buffer.readString();
        this.author = buffer.readString();
        this.main = buffer.readString();
        this.depends = buffer.readStringArray();
        this.softpends = buffer.readStringArray();
        this.enabled = buffer.readBoolean();
        this.name = buffer.readString();
    }
}
