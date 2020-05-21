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
package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

public abstract class PacketAPIProcessCopy extends Packet {

    protected String targetTemplate;
    protected String targetTemplateStorage;
    protected String targetTemplateGroup;

    public PacketAPIProcessCopy() {
    }

    public PacketAPIProcessCopy(String targetTemplate, String targetTemplateStorage, String targetTemplateGroup) {
        this.targetTemplate = targetTemplate;
        this.targetTemplateStorage = targetTemplateStorage;
        this.targetTemplateGroup = targetTemplateGroup;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.targetTemplate);
        buffer.writeString(this.targetTemplateStorage);
        buffer.writeString(this.targetTemplateGroup);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.targetTemplate = buffer.readString();
        this.targetTemplateStorage = buffer.readString();
        this.targetTemplateGroup = buffer.readString();
    }
}
