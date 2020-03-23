package systems.reformcloud.reformcloud2.runner.commands;

import systems.reformcloud.reformcloud2.runner.reformscript.InterpretedReformScript;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterCommand;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterVariable;
import systems.reformcloud.reformcloud2.runner.util.RunnerUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public final class IfCommand extends InterpreterCommand {

    public IfCommand() {
        super("if");
    }

    @Override
    public void execute(@Nonnull String cursorLine, @Nonnull InterpretedReformScript script, @Nonnull Collection<String> allLines) {
        cursorLine = cursorLine.replaceFirst(getCommand() + " ", "");

        String[] splitLine = cursorLine.split(" ");
        Boolean parsed = parse(splitLine[0]);
        if (parsed == null) {
            throw new RuntimeException("Unable to parse second parameter (should be boolean) of line: IF " + cursorLine);
        }

        if (parsed) {
            this.then(splitLine, script, allLines);
        } else {
            this.or(splitLine, script, allLines);
        }
    }

    private void then(@Nonnull String[] splitLine, @Nonnull InterpretedReformScript script, @Nonnull Collection<String> allLines) {
        String then = RunnerUtils.replaceLast(splitLine[1].replaceFirst("THEN\\(", ""), ")", "");
        if (then.trim().isEmpty()) {
            return;
        }

        this.executeCommands(then, script, allLines);
    }

    private void or(@Nonnull String[] splitLine, @Nonnull InterpretedReformScript script, @Nonnull Collection<String> allLines) {
        String or = RunnerUtils.replaceLast(splitLine[1].replaceFirst("OR\\(", ""), ")", "");
        if (or.trim().isEmpty()) {
            return;
        }

        this.executeCommands(or, script, allLines);
    }

    private void executeCommands(@Nonnull String part, @Nonnull InterpretedReformScript script, @Nonnull Collection<String> allLines) {
        for (String parts : part.split(";")) {
            String[] arguments = parts.split(" ");
            InterpreterCommand command = script.getInterpreter().getCommand(arguments[0]);
            if (command == null) {
                continue;
            }

            for (int i = 1; i <= arguments.length; i++) {
                InterpreterVariable variable = script.getInterpreter().getVariable(arguments[i]);
                if (variable == null) {
                    continue;
                }

                parts = parts.replace(arguments[i], variable.unwrap(parts, allLines));
            }

            command.execute(parts, script, allLines);
        }
    }

    @Nullable
    private Boolean parse(@Nonnull String text) {
        if ("true".equals(text) || "false".equals(text)) {
            return "true".equals(text);
        }

        return null;
    }

}
