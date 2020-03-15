package systems.reformcloud.reformcloud2.executor.api.common.utility.list;

public class Quad<F, S, T, X> {

    public Quad(F first, S second, T third, X fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    private final F first;

    private final S second;

    private final T third;

    private final X fourth;

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    public T getThird() {
        return third;
    }

    public X getFourth() {
        return fourth;
    }
}
