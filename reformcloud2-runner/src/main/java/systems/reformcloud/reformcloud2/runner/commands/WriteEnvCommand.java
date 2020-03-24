package systems.reformcloud.reformcloud2.runner.commands;

import systems.reformcloud.reformcloud2.runner.reformscript.InterpretedReformScript;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterCommand;
import systems.reformcloud.reformcloud2.runner.util.RunnerUtils;

import javax.annotation.Nonnull;
import java.util.Collection;

public final class WriteEnvCommand extends InterpreterCommand {

    public WriteEnvCommand() {
        super("write_env");
    }

    @Override
    public void execute(@Nonnull String cursorLine, @Nonnull InterpretedReformScript script, @Nonnull Collection<String> allLines) {
        String executorID = System.getProperty("reformcloud.executor.type");
        if (executorID == null) {
            throw new IllegalArgumentException("Unable to write executor id which is not specified");
        }

        String version = WriteEnvCommand.class.getPackage().getImplementationVersion();
        System.setProperty("reformcloud.runner.version", version);

        RunnerUtils.rewriteFile(script.getScriptPath(), s -> {
            if (s.startsWith("# VARIABLE reformcloud.executor.type=") || s.startsWith("VARIABLE reformcloud.executor.type=")) {
                return "VARIABLE reformcloud.executor.type=" + executorID;
            }

            if (s.startsWith("# VARIABLE reformcloud.runner.version=") || s.startsWith("VARIABLE reformcloud.runner.version=")) {
                s = "VARIABLE reformcloud.runner.version=" + version;
            }

            return s;
        });
    }
}
