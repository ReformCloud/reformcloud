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
package systems.reformcloud.reformcloud2.shared.json;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.Element;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;

import java.math.BigDecimal;
import java.math.BigInteger;

public class GsonElement implements Element {

    private final JsonElement gsonElement;

    public GsonElement(JsonElement gsonElement) {
        this.gsonElement = gsonElement;
    }

    @Override
    public boolean isObject() {
        return this.gsonElement.isJsonObject();
    }

    @Override
    public boolean isPrimitive() {
        return this.gsonElement.isJsonPrimitive();
    }

    @Override
    public boolean isNull() {
        return this.gsonElement.isJsonNull();
    }

    @Override
    public boolean getAsBoolean() {
        return this.gsonElement.getAsBoolean();
    }

    @Override
    public @NotNull Number getAsNumber() {
        return this.gsonElement.getAsNumber();
    }

    @Override
    public @NotNull String getAsString() {
        return this.gsonElement.getAsString();
    }

    @Override
    public double getAsDouble() {
        return this.gsonElement.getAsDouble();
    }

    @Override
    public float getAsFloat() {
        return this.gsonElement.getAsFloat();
    }

    @Override
    public long getAsLong() {
        return this.gsonElement.getAsLong();
    }

    @Override
    public int getAsInt() {
        return this.gsonElement.getAsInt();
    }

    @Override
    public byte getAsByte() {
        return this.gsonElement.getAsByte();
    }

    @Override
    public @NotNull BigDecimal getAsBigDecimal() {
        return this.gsonElement.getAsBigDecimal();
    }

    @Override
    public @NotNull BigInteger getAsBigInteger() {
        return this.gsonElement.getAsBigInteger();
    }

    @Override
    public short getAsShort() {
        return this.gsonElement.getAsShort();
    }

    @Override
    public @NotNull Element clone() {
        return new GsonElement(this.gsonElement.deepCopy());
    }

    @NotNull
    @Override
    public String toString() {
        return this.gsonElement.toString();
    }

    @NotNull
    JsonElement getGsonElement() {
        return this.gsonElement;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {

    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {

    }
}
