package systems.reformcloud.reformcloud2.runner;

import systems.reformcloud.reformcloud2.runner.util.RunnerUtils;

import javax.annotation.Nonnull;

public final class RunnerExecutor {

    public static synchronized void main(@Nonnull String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("io.netty.noPreferDirect", "true");
        System.setProperty("client.encoding.override", "UTF-8");

        checkForAdministratorAndWarn();

        Runner runner = new Runner(args);
        runner.bootstrap();
    }

    private static void checkForAdministratorAndWarn() {
        String user = System.getProperty("user.name");
        String os = System.getProperty("os.name");

        if (os.contains("mac") || os.contains("darwin")) {
            // mac has no default administrative user
            return;
        }

        if (os.contains("win") && user.contains("admin")) {
            RunnerUtils.sendBigWarning("You are on an administrative account. Please think about " +
                    "creating and using a non-administrative user for more security");
            return;
        }

        if (os.contains("nux") && user.equals("root")) {
            RunnerUtils.sendBigWarning("You are on an administrative account. Please think about " +
                    "creating and using a non-administrative user for more security");
        }
    }
}
