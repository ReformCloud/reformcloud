package de.klaro.reformcloud2.executor.api.common.utility.list;

public final class Trio<F, S, T> {

    public Trio(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    private final F first;

    private final S second;

    private final T third;

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    public T getThird() {
        return third;
    }
}
