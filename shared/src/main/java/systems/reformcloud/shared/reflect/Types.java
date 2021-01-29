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
package systems.reformcloud.shared.reflect;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

public final class Types {

  private static final Type[] EMPTY_TYPE_ARRAY = new Type[0];

  private Types() {
    throw new UnsupportedOperationException();
  }

  public static Type fromClass(@NotNull Class<?> clazz) {
    final Type superclass = clazz.getGenericSuperclass();
    if (!(superclass instanceof Class)) {
      return fromActualTypeArgument(((ParameterizedType) superclass).getActualTypeArguments()[0]);
    } else {
      throw new IllegalStateException("Missing type parameter");
    }
  }

  private static Type fromActualTypeArgument(@NotNull Type type) {
    if (type instanceof Class) {
      final Class<?> clazz = (Class<?>) type;
      return clazz.isArray() ? new GenericArrayTypeImplementation(fromActualTypeArgument(clazz.getComponentType())) : clazz;
    } else if (type instanceof WildcardType) {
      final WildcardType wildcardType = (WildcardType) type;
      return new WildcardTypeImplementation(wildcardType.getUpperBounds(), wildcardType.getLowerBounds());
    } else if (type instanceof GenericArrayType) {
      return new GenericArrayTypeImplementation(((GenericArrayType) type).getGenericComponentType());
    } else if (type instanceof ParameterizedType) {
      final ParameterizedType parameterizedType = (ParameterizedType) type;
      return new ParameterizedTypeImplementation(parameterizedType.getActualTypeArguments(), parameterizedType.getRawType(), parameterizedType.getOwnerType());
    } else {
      return type;
    }
  }

  private static final class GenericArrayTypeImplementation implements GenericArrayType, Serializable {
    private static final long serialVersionUID = 0;
    private final Type genericComponentType;

    private GenericArrayTypeImplementation(Type genericComponentType) {
      this.genericComponentType = genericComponentType;
    }

    @Override
    public Type getGenericComponentType() {
      return this.genericComponentType;
    }
  }

  private static final class WildcardTypeImplementation implements WildcardType, Serializable {
    private static final long serialVersionUID = 0;
    private final Type[] upperBounds;
    private final Type[] lowerBounds;

    private WildcardTypeImplementation(Type[] upperBounds, Type[] lowerBounds) {
      if (lowerBounds.length == 1) {
        this.upperBounds = new Type[]{Object.class};
        this.lowerBounds = new Type[]{fromActualTypeArgument(lowerBounds[0])};
      } else {
        this.upperBounds = new Type[]{fromActualTypeArgument(upperBounds[0])};
        this.lowerBounds = EMPTY_TYPE_ARRAY;
      }
    }

    @Override
    public Type[] getUpperBounds() {
      return this.upperBounds;
    }

    @Override
    public Type[] getLowerBounds() {
      return this.lowerBounds;
    }
  }

  private static final class ParameterizedTypeImplementation implements ParameterizedType, Serializable {
    private final Type[] typeArguments;
    private final Type rawType;
    private final Type ownerType;

    public ParameterizedTypeImplementation(Type[] typeArguments, Type rawType, Type ownerType) {
      this.rawType = fromActualTypeArgument(rawType);
      this.ownerType = ownerType == null ? null : fromActualTypeArgument(ownerType);

      this.typeArguments = new Type[typeArguments.length];
      for (int i = 0; i < typeArguments.length; i++) {
        this.typeArguments[i] = fromActualTypeArgument(typeArguments[i]);
      }
    }

    @Override
    public Type[] getActualTypeArguments() {
      return this.typeArguments;
    }

    @Override
    public Type getRawType() {
      return this.rawType;
    }

    @Override
    public Type getOwnerType() {
      return this.ownerType;
    }
  }
}
