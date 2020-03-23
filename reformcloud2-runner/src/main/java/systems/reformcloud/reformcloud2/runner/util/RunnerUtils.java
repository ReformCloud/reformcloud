package systems.reformcloud.reformcloud2.runner.util;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

public final class RunnerUtils {

    private RunnerUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * The path where the executor file is located
     */
    public static final Path EXECUTOR_PATH = Paths.get("reformcloud/.bin/executor.jar");

    /**
     * The default discord invite for the reformcloud discord
     */
    public static final String DISCORD_INVITE = "https://discord.gg/uskXdVZ";

    /**
     * The base url of the reformcloud github repository
     */
    public static final String REPO_BASE_URL = "https://github.com/derklaro/reformcloud2/";

    /**
     * The url of the README file in the reformcloud repository
     */
    public static final String README_URL = REPO_BASE_URL + "blob/master/.github/README.md";

    /**
     * All available executors
     */
    public static final Collection<String> AVAILABLE_EXECUTORS = Arrays.asList("node", "controller", "client");

    /**
     * Handles the given error and prints an error message to the console
     *
     * @param message   The message of the error which contains some extra information
     * @param throwable The exception itself which should get handled
     */
    public static void handleError(@Nonnull String message, @Nonnull Throwable throwable) {
        System.err.println(" * " + message);
        System.err.println(" * Do not report this as an error to reformcloud!");
        System.err.println(" * Just contact the support of reformcloud to get further help about this");
        System.err.println(" * You can either chat with us at " + DISCORD_INVITE);
        System.err.println(" * Or create an issue as a support request on GitHub: " + REPO_BASE_URL);
        System.err.println(" * Please include the following lines to get the full support");
        System.err.println(" ----------------| Begin of stacktrace |---------------- ");
        throwable.printStackTrace(System.err);
        System.err.println(" ----------------|  End of stacktrace  |---------------- ");
    }

    /**
     * Debugs the given message if the debug mode is enabled
     *
     * @param line The message line which should get debugged if debug logging is enabled
     */
    public static void debug(@Nonnull String line) {
        if (Boolean.getBoolean("reformcloud.dev.mode")) {
            System.out.println("DEBUG: " + line);
        }
    }

    /**
     * Replaces the last occurrence of the regex in the given string
     *
     * @param text        The text in which the string should get removed
     * @param regex       The regex of the last string which should get removed
     * @param replacement The replacement which should replace the last occurrence
     * @return The newly formatted string
     */
    @Nonnull
    public static String replaceLast(@Nonnull String text, @Nonnull String regex, @Nonnull String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }
}
