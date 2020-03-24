package systems.reformcloud.reformcloud2.runner.commands;

import systems.reformcloud.reformcloud2.runner.reformscript.InterpretedReformScript;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterCommand;

import javax.annotation.Nonnull;
import java.util.Collection;

public final class PrintlnCommand extends InterpreterCommand {

    public PrintlnCommand() {
        super("println");
    }

    @Override
    public void execute(@Nonnull String cursorLine, @Nonnull InterpretedReformScript script, @Nonnull Collection<String> allLines) {
        String line = cursorLine.replaceFirst(getCommand(), "");
        System.out.println(line.trim().isEmpty() ? "" : line.replaceFirst(" ", ""));
    }
}
