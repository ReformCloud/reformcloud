package systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.dump;

import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class CommandDump extends GlobalCommand {

    public CommandDump(DumpUtil specificDump) {
        super("dump", "reformcloud.command.dump",
                "Dumps the full cloud system to the paste server", new ArrayList<>());
        this.specificDumper = specificDump;
    }

    private final DumpUtil specificDumper;

    @Override
    public boolean handleCommand(CommandSource commandSource, String[] strings) {
        commandSource.sendMessage(LanguageManager.get("command-dump-creating"));
        String dump = createFullDump();
        commandSource.sendMessage(LanguageManager.get("command-sump-created", pasteDump(dump)));
        return true;
    }

    private String createFullDump() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("-------- ReformCloud Support Dump --------");
        stringBuilder.append("\n\n");

        appendGeneralInformation(stringBuilder);
        dumpCloudInfo(stringBuilder);
        dumpSystemInfo(stringBuilder);

        stringBuilder.append("\n");

        this.specificDumper.appendCurrentDump(stringBuilder);

        stringBuilder.append("--- Current Thread ---");
        stringBuilder.append("\n");

        dumpThreadInfo(Thread.currentThread(), stringBuilder);

        stringBuilder.append("--- Other Threads ---");
        stringBuilder.append("\n");

        Thread.getAllStackTraces().keySet().stream().filter(e -> e.getId() != Thread.currentThread().getId())
                .forEach(e -> dumpThreadInfo(e, stringBuilder));

        return stringBuilder.toString();
    }

    private static void dumpThreadInfo(Thread thread, StringBuilder builder) {
        builder.append("Name: ").append(thread.getName()).append("\n");
        builder.append("Id: ").append(thread.getId()).append("\n");
        builder.append("State: ").append(thread.getState().name()).append("\n");
        builder.append("Priority: ").append(getPriority(thread)).append("\n");
        builder.append("ClassLoader: ").append(getClassLoader(thread)).append("\n");
        builder.append("Alive/Daemon/Interrupted: ").append(thread.isAlive()).append("/")
                .append(thread.isDaemon()).append("/").append(thread.isInterrupted()).append("\n");

        dumpThread(thread.getStackTrace(), builder);
    }

    private static void dumpThread(StackTraceElement[] throwable, StringBuilder builder) {
        builder.append("\n").append("Last Stacktrace: ").append("\n");
        if (throwable.length > 0) {
            Arrays.stream(throwable).forEach(e -> builder.append("\t").append("at ").append(e).append("\n"));
        } else {
            builder.append("none").append("\n");
        }

        builder.append("\n\n");
    }

    private static void appendGeneralInformation(StringBuilder stringBuilder) {
        stringBuilder
                .append("Time: ")
                .append(new SimpleDateFormat().format(System.currentTimeMillis()))
                .append("\n")
                .append("This dump report got created automatically, please send the link to this document to the support they CAN ask for more information")
                .append("\n\n");
    }

    private static void dumpCloudInfo(StringBuilder stringBuilder) {
        stringBuilder
                .append("--- Cloud System Info ---")
                .append("\n")
                .append("Version: ")
                .append(System.getProperty("reformcloud.runner.version"))
                .append(" (")
                .append(System.getProperty("reformcloud.runner.specification"))
                .append(")")
                .append("\n")
                .append("Executor: ")
                .append(ExecutorAPI.getInstance().getType().getId())
                .append(" (")
                .append(ExecutorAPI.getInstance().getType().name())
                .append(")")
                .append("\n\n");
    }

    private static void dumpSystemInfo(StringBuilder stringBuilder) {
        stringBuilder
                .append("--- User Info ---")
                .append("\n")
                .append("User-Name: ")
                .append(System.getProperty("user.name"))
                .append("\n")
                .append("User-Home path: ")
                .append(System.getProperty("user.home"))
                .append("\n")
                .append("Current user working path: ")
                .append(System.getProperty("user.dir"))
                .append("\n\n");

        stringBuilder
                .append("--- Operation System Info ---")
                .append("\n")
                .append(System.getProperty("os.name"))
                .append("(")
                .append(System.getProperty("os.arch"))
                .append(") version ")
                .append(System.getProperty("os.version"))
                .append("\n\n");

        stringBuilder
                .append("--- Java Version Info ---")
                .append("\n")
                .append(System.getProperty("java.version"))
                .append(", ")
                .append(System.getProperty("java.vendor"))
                .append("\n\n");

        stringBuilder
                .append("--- Java VM Info ---")
                .append("\n")
                .append(System.getProperty("java.vm.name"))
                .append("(")
                .append(System.getProperty("java.vm.info"))
                .append("), ")
                .append(System.getProperty("java.vm.vendor"))
                .append("\n\n");

        Collection<String> jvmFlags = Links.allOf(CommonHelper.inputArguments(), e -> e.startsWith("-X"));
        Collection<String> systemEnvProperties = System.getenv().entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.toList());
        Collection<String> systemProperties = Links.allOf(CommonHelper.inputArguments(), e -> e.startsWith("-D"));

        stringBuilder
                .append("--- Java Runtime Info ---")
                .append("\n")
                .append("Runtime Flags: ")
                .append(String.format("%d total, %s", CommonHelper.inputArguments().size(),
                        String.join(" ", CommonHelper.inputArguments())))
                .append("\n")
                .append("JVM Flags: ")
                .append(String.format("%d total, %s", jvmFlags.size(),
                        String.join(" ", jvmFlags)))
                .append("\n")
                .append("System environment Variables: ")
                .append(String.format("%d total, %s", systemEnvProperties.size(),
                        String.join(" ", systemEnvProperties)))
                .append("\n")
                .append("System Properties: ")
                .append(String.format("%d total, %s", systemProperties.size(),
                        String.join(" ", systemProperties)))
                .append("\n\n");

        Runtime runtime = Runtime.getRuntime();
        long max = runtime.maxMemory() / 1024L / 1024L;
        long total = runtime.totalMemory() / 1024L / 1024L;
        long free = runtime.freeMemory() / 1024L / 1024L;

        stringBuilder
                .append("--- Memory Info ---")
                .append("\n")
                .append(runtime.freeMemory())
                .append(" bytes (")
                .append(free)
                .append(" MB) / ")
                .append(runtime.totalMemory())
                .append(" bytes (")
                .append(total)
                .append(" MB) up to ")
                .append(runtime.maxMemory())
                .append(" bytes (")
                .append(max)
                .append(" MB)")
                .append("\n\n");

        stringBuilder
                .append("--- Central Processing Unit Info ---")
                .append("\n")
                .append("Cores: ")
                .append(Runtime.getRuntime().availableProcessors())
                .append("\n")
                .append("System CPU Usage: ")
                .append(CommonHelper.cpuUsageSystem())
                .append("\n")
                .append("Process CPU Usage: ")
                .append(CommonHelper.operatingSystemMXBean().getProcessCpuLoad() * 100)
                .append("\n");
    }

    private static String getPriority(Thread thread) {
        if (thread.getPriority() == Thread.MIN_PRIORITY) {
            return "Min-Priority (" + Thread.MIN_PRIORITY + ")";
        }

        if (thread.getPriority() == Thread.NORM_PRIORITY) {
            return "Normal-Priority (" + Thread.NORM_PRIORITY + ")";
        }

        if (thread.getPriority() == Thread.MAX_PRIORITY) {
            return "Max-Priority (" + Thread.MAX_PRIORITY + ")";
        }

        return "Custom Priority (" + thread.getPriority() + ")";
    }

    private static String getClassLoader(Thread thread) {
        return thread.getContextClassLoader() == null ? "sun.misc.Launcher$AppClassLoader" : thread.getContextClassLoader().getClass().getName();
    }

    private static String pasteDump(String dump) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("https://paste.reformcloud.systems/documents").openConnection();
            httpURLConnection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11"
            );
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.connect();

            try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
                outputStream.write(dump.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            }

            try (InputStream inputStream = httpURLConnection.getInputStream()) {
                return "https://paste.reformcloud.systems/" + getResult(inputStream);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        return "Unable to create dump on https://paste.reformcloud.systems, please contact the support for help";
    }

    private static String getResult(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;

        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }

        String parsed = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
        return new JsonConfiguration(parsed).getString("key");
    }
}
