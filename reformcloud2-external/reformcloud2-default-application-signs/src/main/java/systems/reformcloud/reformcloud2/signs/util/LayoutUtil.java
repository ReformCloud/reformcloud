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
