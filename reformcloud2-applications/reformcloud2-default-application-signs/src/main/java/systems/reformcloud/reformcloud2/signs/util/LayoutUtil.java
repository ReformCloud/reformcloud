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
package systems.reformcloud.reformcloud2.signs.util;

import systems.reformcloud.reformcloud2.signs.util.sign.config.SignConfig;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignLayout;
import systems.reformcloud.reformcloud2.signs.util.sign.config.util.LayoutContext;

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

        int i = atomicInteger.incrementAndGet();
        if (list.size() <= i) {
            atomicInteger.set(-1);
            i = 0;
        }

        return Optional.of(list.get(i));
    }

    public static Optional<SignLayout> getLayoutFor(String group, SignConfig config) {
        SignLayout out = null;

        for (SignLayout layout : config.getLayouts()) {
            if (layout.getTarget() != null
                    && layout.getContext().equals(LayoutContext.GROUP_BOUND)
                    && layout.getTarget().equals(group)
            ) {
                return Optional.of(layout);
            }

            if (layout.getContext().equals(LayoutContext.GLOBAL) && out == null) {
                out = layout;
            }
        }

        return Optional.ofNullable(out);
    }
}
