package systems.reformcloud.reformcloud2.runner.commands;

import systems.reformcloud.reformcloud2.runner.reformscript.InterpretedReformScript;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterCommand;

import javax.annotation.Nonnull;
import java.util.Collection;

public final class VariableCommand extends InterpreterCommand {

    public VariableCommand() {
        super("variable");
    }

    @Override
    public void execute(@Nonnull String cursorLine, @Nonnull InterpretedReformScript script, @Nonnull Collection<String> allLines) {
        cursorLine = cursorLine.replaceFirst(getCommand() + " ", "");
        String[] parts = cursorLine.split("=");
        if (parts.length != 2) {
            throw new RuntimeException("Unable to execute Variable command correctly! Missing identifiers");
        }

        System.setProperty(parts[0], parts[1]);
    }
}
