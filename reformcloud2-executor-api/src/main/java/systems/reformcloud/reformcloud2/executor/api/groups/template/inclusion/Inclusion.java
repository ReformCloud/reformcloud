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
package systems.reformcloud.reformcloud2.executor.api.groups.template.inclusion;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.enums.EnumUtil;
import systems.reformcloud.reformcloud2.executor.api.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;

public class Inclusion implements SerializableObject {

    private String key;
    private String backend;
    private InclusionLoadType inclusionLoadType;

    @ApiStatus.Internal
    public Inclusion() {
    }

    public Inclusion(String key, String backend, InclusionLoadType inclusionLoadType) {
        this.key = key;
        this.backend = backend;
        this.inclusionLoadType = inclusionLoadType;
    }

    public String getKey() {
        return this.key;
    }

    public String getBackend() {
        return this.backend;
    }

    public InclusionLoadType getInclusionLoadType() {
        return this.inclusionLoadType;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.key);
        buffer.writeString(this.backend);
        buffer.writeVarInt(this.inclusionLoadType.ordinal());
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.key = buffer.readString();
        this.backend = buffer.readString();
        this.inclusionLoadType = EnumUtil.findEnumFieldByIndex(InclusionLoadType.class, buffer.readVarInt()).orElse(null);
    }

    public enum InclusionLoadType {

        PRE,
        PAST
    }
}
