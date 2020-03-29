package systems.reformcloud.reformcloud2.runner.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public final class RunnerUtils {

    private RunnerUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * The path where the executor file is located
     */
    public static final Path EXECUTOR_PATH = Paths.get("reformcloud/.bin/executor.jar");

    /**
     * The path of the folder in which the application updates are located
     */
    public static final Path APP_UPDATE_FOLDER = Paths.get("reformcloud/.update/apps");

    /**
     * The location of the runner in the node and client env
     */
    public static final Path RUNNER_FILES_FILE = Paths.get("reformcloud/files/runner.jar");

    /**
     * The file where the reform script is located by default
     */
    public static final File GLOBAL_SCRIPT_FILE = new File("global.reformscript");

    /**
     * The default discord invite for the reformcloud discord
     */
    public static final String DISCORD_INVITE = "https://discord.gg/uskXdVZ";

    /**
     * The base url of the reformcloud github repository
     */
    public static final String REPO_BASE_URL = "https://github.com/derklaro/reformcloud2/";

    /**
     * The file download format string
     */
    public static final String FILE_DOWNLOAD_FORMAT = "Downloading file %s... Size: %s";

    /**
     * The file downloaded format string
     */
    public static final String FILE_DOWNLOADED_FORMAT = "Download of file %s was completed successfully after %dms";

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
    public static void handleError(@NotNull String message, @NotNull Throwable throwable) {
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
    public static void debug(@NotNull String line) {
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
    @NotNull
    public static String replaceLast(@NotNull String text, @NotNull String regex, @NotNull String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }

    /**
     * Copies a compiled file from the source location to the given target location
     *
     * @param from The location of the compiled file which should get copied
     * @param to   The target of the file which should get copied
     */
    public static void copyCompiledFile(@NotNull String from, @NotNull File to) {
        copyCompiledFile(from, to.toPath());
    }

    /**
     * Copies a compiled file from the source location to the given target location
     *
     * @param from The location of the compiled file which should get copied
     * @param to   The target of the file which should get copied
     */
    public static void copyCompiledFile(@NotNull String from, @NotNull Path to) {
        try (InputStream stream = RunnerUtils.class.getClassLoader().getResourceAsStream(from)) {
            if (stream == null) {
                throw new RuntimeException("Unable to find compiled default reformscript. Please validate");
            }

            if (to.getParent() != null && !Files.exists(to.getParent())) {
                Files.createDirectories(to.getParent());
            }

            Files.copy(stream, to, StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Opens a connection to the remote url and tries to read the properties from the stream of the connection
     *
     * @param url The url behind which the properties are located
     * @return The loaded properties from the input stream
     */
    @NotNull
    public static Properties loadProperties(@NotNull String url) {
        Properties properties = new Properties();
        openConnection(url, stream -> {
            try {
                properties.load(stream);
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        });
        return properties;
    }

    /**
     * Opens a connection to the remote url and accepts th resulting input steam to the given handler
     *
     * @param url                 The url which should get opened
     * @param inputStreamConsumer The consumer which should accept the input stream of the connection
     */
    public static void openConnection(@NotNull String url, @NotNull Consumer<InputStream> inputStreamConsumer) {
        openURLConnection(url, httpURLConnection -> {
            try (InputStream stream = httpURLConnection.getInputStream()) {
                inputStreamConsumer.accept(stream);
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    /**
     * Downloads a file from the specified url and copies it to the given path
     *
     * @param url    The url of the file
     * @param target The target path where the file should get saved to
     */
    public static void downloadFile(@NotNull String url, @NotNull Path target) {
        openURLConnection(url, connection -> {
            System.out.println(String.format(RunnerUtils.FILE_DOWNLOAD_FORMAT, url, RunnerUtils.getSize(connection.getContentLengthLong())));
            long start = System.currentTimeMillis();

            try (InputStream stream = connection.getInputStream()) {
                Files.copy(stream, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (final IOException ex) {
                ex.printStackTrace();
            }

            System.out.println(String.format(RunnerUtils.FILE_DOWNLOADED_FORMAT, url, System.currentTimeMillis() - start));
        });
    }

    /**
     * Opens a connection to the remote url and accepts th resulting input steam to the given handler
     *
     * @param url                The url which should get opened
     * @param connectionConsumer The consumer which should accept the connection which was established
     */
    public static void openURLConnection(@NotNull String url, @NotNull Consumer<HttpURLConnection> connectionConsumer) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11"
            );
            httpURLConnection.setDoOutput(false);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setInstanceFollowRedirects(true);
            httpURLConnection.connect();
            connectionConsumer.accept(httpURLConnection);
            httpURLConnection.disconnect();
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Iterates through all files in a directory and waits until the filter accepts the path
     *
     * @param folderToSearch The folder in which should get searched for the file
     * @param filter         The filter which can filter out the file it needs
     * @param pathFilter     The path filter which should pre filter the input files
     * @return The path of the filer which passes the filer or {@code null} if no file passes the filter
     */
    @Nullable
    public static Path findFile(@NotNull Path folderToSearch, @NotNull Predicate<Path> filter, @NotNull DirectoryStream.Filter<Path> pathFilter) {
        if (!Files.exists(folderToSearch) || !Files.isDirectory(folderToSearch)) {
            throw new RuntimeException("Tried to search in a folder which does not exists or is not a folder");
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderToSearch, pathFilter)) {
            for (Path path : stream) {
                if (filter.test(path)) {
                    return path;
                }
            }
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }

        return null;
    }

    /**
     * Tries to delete the given file
     *
     * @param path The path of the file which should get deleted
     */
    public static void deleteFileIfExists(@NotNull Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Copies a file from the source location to the new target
     *
     * @param from The source location of the file
     * @param to   The target location of the file
     */
    public static void copy(@NotNull Path from, @NotNull Path to) {
        try {
            if (to.getParent() != null && !Files.exists(to.getParent())) {
                Files.createDirectories(to.getParent());
            }

            Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Rewrites the complete specified file with
     *
     * @param path     The path of the file which should get re written
     * @param operator The handler for each line in the file to replace a line
     */
    public static void rewriteFile(@NotNull Path path, @NotNull UnaryOperator<String> operator) {
        try {
            List<String> list = Files.readAllLines(path);
            List<String> newLine = new ArrayList<>();

            list.forEach(s -> newLine.add(operator.apply(s)));

            try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.UTF_8), true)) {
                newLine.forEach(s -> {
                    printWriter.write(s + "\n");
                    printWriter.flush();
                });
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Calculates the size of the input stream of the connection
     *
     * @param size The size as long of the stream
     * @return The formatted size as result
     */
    @NotNull
    public static String getSize(long size) {
        if (size >= 1048576L) {
            return Math.round((float) size / 1048576.0F) + "MB";
        }

        if (size >= 1024) {
            return Math.round((float) size / 1024.0F) + "KB";
        }

        return Math.round(size) + "B";
    }
}
