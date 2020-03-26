package systems.reformcloud.reformcloud2.runner;

import javax.annotation.Nonnull;

public final class RunnerExecutor {

    public static synchronized void main(@Nonnull String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("io.netty.noPreferDirect", "true");

        Runner runner = new Runner(args);
        runner.bootstrap();
    }
}
