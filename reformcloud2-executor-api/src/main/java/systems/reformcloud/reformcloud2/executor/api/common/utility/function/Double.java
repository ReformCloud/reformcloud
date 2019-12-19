package systems.reformcloud.reformcloud2.executor.api.common.utility.function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Double<F, S> {

    public Double(@Nonnull F first, @Nullable S second) {
        this.first = first;
        this.second = second;
    }

    private final F first;

    private final S second;

    /**
     * @return The key of this pair
     */
    @Nonnull
    public F getFirst() {
        return first;
    }

    /**
     * @return The value of the this pair or {@code null} if the value is undefined
     */
    @Nullable
    public S getSecond() {
        return second;
    }
}
