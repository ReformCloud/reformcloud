package systems.reformcloud.reformcloud2.runner;

import org.jetbrains.annotations.NotNull;

public final class RunnerExecutor {

    public static synchronized void main(@NotNull String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("io.netty.noPreferDirect", "true");

        Runner runner = new Runner(args);
        runner.bootstrap();
    }
}
