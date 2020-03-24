package systems.reformcloud.reformcloud2.runner.reformscript.utils;

import systems.reformcloud.reformcloud2.runner.reformscript.InterpretedReformScript;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Represents a task in a reform script. It's like a method in java code
 */
public abstract class InterpreterTask {

    /**
     * @return The name of the method
     */
    @Nonnull
    public abstract String getName();

    /**
     * Executes the current task
     *
     * @param callerLine The line from which the tasks gets called
     * @param script     The script from which the caller is called which calls the task
     * @param allLines   All lines of the interpreted script
     */
    public abstract void executeTask(@Nonnull String callerLine, @Nonnull InterpretedReformScript script, @Nonnull Collection<String> allLines);
}
