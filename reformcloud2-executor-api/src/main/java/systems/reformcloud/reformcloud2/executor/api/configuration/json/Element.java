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
package systems.reformcloud.reformcloud2.executor.api.configuration.json;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.types.Array;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.types.Null;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.types.Object;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.types.Primitive;
import systems.reformcloud.reformcloud2.executor.api.network.data.SerializableObject;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface Element extends SerializableObject, Cloneable {

  boolean isObject();

  boolean isPrimitive();

  boolean isArray();

  boolean isNull();

  boolean getAsBoolean();

  @NotNull
  Number getAsNumber();

  @NotNull
  Object getAsObject();

  @NotNull
  Array getAsArray();

  @NotNull
  Primitive getAsPrimitive();

  @NotNull
  Null getAsNull();

  @NotNull
  String getAsString();

  double getAsDouble();

  float getAsFloat();

  long getAsLong();

  int getAsInt();

  byte getAsByte();

  @NotNull
  BigDecimal getAsBigDecimal();

  @NotNull
  BigInteger getAsBigInteger();

  short getAsShort();

  @NotNull
  @Override
  String toString();
}
