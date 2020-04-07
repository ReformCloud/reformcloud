package systems.reformcloud.reformcloud2.runner.commands;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.runner.reformscript.InterpretedReformScript;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterCommand;
import systems.reformcloud.reformcloud2.runner.util.RunnerUtils;

import java.util.Collection;

public final class VariableCommand extends InterpreterCommand {

    public VariableCommand() {
        super("variable");
    }

    @Override
    public void execute(@NotNull String cursorLine, @NotNull InterpretedReformScript script, @NotNull Collection<String> allLines) {
        cursorLine = cursorLine.replaceFirst(getCommand() + " ", "");
        String[] parts = cursorLine.split("=");
        if (parts.length != 2) {
            throw new RuntimeException("Unable to execute Variable command correctly! Missing identifiers");
        }

        if (System.getProperty(parts[0]) != null) {
            RunnerUtils.debug("Not setting system env variable " + parts[0] + " because it's already set");
            return;
        }

        System.setProperty(parts[0], parts[1]);
    }
}
