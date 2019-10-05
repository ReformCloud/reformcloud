package systems.reformcloud.reformcloud2.executor.api.common.utility.function;

public final class Double<F, S> {

    public Double(F first, S second) {
        this.first = first;
        this.second = second;
    }

    private final F first;

    private final S second;

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }
}
