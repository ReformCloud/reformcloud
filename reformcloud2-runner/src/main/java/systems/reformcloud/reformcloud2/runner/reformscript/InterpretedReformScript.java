package systems.reformcloud.reformcloud2.runner.reformscript;

import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterTask;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Represents a reform script which got interpreted and is ready to execute
 */
public interface InterpretedReformScript {

    /**
     * @return The interpreter which interpreted the current script
     */
    @Nonnull
    ReformScriptInterpreter getInterpreter();

    /**
     * @return All tasks of the current script
     */
    @Nonnull
    Collection<InterpreterTask> getAllTasks();

    /**
     * Executes the current script completely with all commands and variables which are in it
     */
    void execute();
}
