/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
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
package systems.reformcloud.examples.network;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.network.data.ProtocolBuffer;
import systems.reformcloud.network.data.SerializableObject;

/**
 * An example object which can get written into a protocol buffer
 */
public class ExampleSerializableObject implements SerializableObject {

  private String exampleString;
  private int exampleInt;
  private boolean exampleBoolean;

  public ExampleSerializableObject() {
    // The no args constructor is required in every serializable object for the decode process
  }

  @Override
  public void write(@NotNull ProtocolBuffer buffer) {
    buffer.writeString(this.exampleString);
    buffer.writeInt(this.exampleInt);
    buffer.writeBoolean(this.exampleBoolean);
  }

  @Override
  public void read(@NotNull ProtocolBuffer buffer) {
    // Read the objects in the same order as written into the buffer by the write() method
    this.exampleString = buffer.readString();
    this.exampleInt = buffer.readInt();
    this.exampleBoolean = buffer.readBoolean();
  }
}
