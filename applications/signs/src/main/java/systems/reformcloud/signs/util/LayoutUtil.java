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
package systems.reformcloud.signs.util;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.signs.util.sign.config.SignConfig;
import systems.reformcloud.signs.util.sign.config.SignLayout;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public final class LayoutUtil {

  private LayoutUtil() {
    throw new UnsupportedOperationException();
  }

  public static <T> Optional<T> getNextAndCheckFor(List<T> list, AtomicInteger atomicInteger) {
    if (list.isEmpty()) {
      return Optional.empty();
    }

    if (list.size() == 1) {
      return Optional.of(list.get(0));
    }

    return Optional.of(list.get(atomicInteger.get()));
  }

  public static <T> void flush(@NotNull List<T> list, @NotNull AtomicInteger integer) {
    if (list.isEmpty() || list.size() == 1) {
      return;
    }

    if (integer.incrementAndGet() >= list.size()) {
      integer.set(0);
    }
  }

  @NotNull
  public static Optional<SignLayout> getLayoutFor(@NotNull String group, @NotNull SignConfig config) {
    SignLayout out = null;

    for (SignLayout layout : config.getLayouts()) {
      if (layout.getTarget() != null && layout.getTarget().equals(group)) {
        return Optional.of(layout);
      }

      if (layout.getTarget() == null && out == null) {
        out = layout;
      }
    }

    return Optional.ofNullable(out);
  }
}
