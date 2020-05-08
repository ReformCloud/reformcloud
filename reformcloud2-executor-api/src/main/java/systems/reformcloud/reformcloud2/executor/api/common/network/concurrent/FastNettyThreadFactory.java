package systems.reformcloud.reformcloud2.executor.api.common.network.concurrent;

import io.netty.util.concurrent.FastThreadLocalThread;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class FastNettyThreadFactory implements ThreadFactory {

    public FastNettyThreadFactory(String nameFormat) {
        this.nameFormat = nameFormat;
    }

    private final String nameFormat;

    private final AtomicInteger threadNumber = new AtomicInteger();

    @Override
    public Thread newThread(@NotNull Runnable r) {
        String name = String.format(this.nameFormat, threadNumber.getAndIncrement());
        return new FastThreadLocalThread(r, name);
    }
}
