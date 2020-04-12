package systems.reformcloud.reformcloud2.runner.reformscript;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterTask;

import java.nio.file.Path;
import java.util.Collection;

/**
 * Represents a reform script which got interpreted and is ready to execute
 */
public interface InterpretedReformScript {

    /**
     * @return The interpreter which interpreted the current script
     */
    @NotNull
    ReformScriptInterpreter getInterpreter();

    /**
     * @return All tasks of the current script
     */
    @NotNull
    Collection<InterpreterTask> getAllTasks();

    /**
     * @return The path to the file which contains the script
     */
    @NotNull
    Path getScriptPath();

    /**
     * Executes the current script completely with all commands and variables which are in it
     */
    void execute();
}
