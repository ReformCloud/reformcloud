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
package systems.reformcloud.reformcloud2.signs.util.sign.config;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;

public class SignSubLayout implements SerializableObject {

    private String[] lines;
    private String block;
    private int subID;

    public SignSubLayout() {
    }

    public SignSubLayout(String[] lines, String block, int subID) {
        this.lines = lines;
        this.block = block;
        this.subID = subID;
    }

    public String[] getLines() {
        return this.lines;
    }

    public String getBlock() {
        return this.block;
    }

    public int getSubID() {
        return this.subID;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeStringArrays(this.lines);
        buffer.writeString(this.block);
        buffer.writeInt(this.subID);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.lines = buffer.readStringArrays();
        this.block = buffer.readString();
        this.subID = buffer.readInt();
    }
}
