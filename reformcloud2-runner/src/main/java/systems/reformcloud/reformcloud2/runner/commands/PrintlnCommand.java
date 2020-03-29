package systems.reformcloud.reformcloud2.runner.commands;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.runner.reformscript.InterpretedReformScript;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterCommand;

import java.util.Collection;

public final class PrintlnCommand extends InterpreterCommand {

    public PrintlnCommand() {
        super("println");
    }

    @Override
    public void execute(@NotNull String cursorLine, @NotNull InterpretedReformScript script, @NotNull Collection<String> allLines) {
        String line = cursorLine.replaceFirst(getCommand(), "");
        System.out.println(line.trim().isEmpty() ? "" : line.replaceFirst(" ", ""));
    }
}
