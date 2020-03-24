package systems.reformcloud.reformcloud2.runner.reformscript;

import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterCommand;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterVariable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Path;

/**
 * Represents an interpreter for reform scripts. It can read the scripts and register placeholder as
 * well as commands for the script itself to run correctly
 */
public interface ReformScriptInterpreter {

    /**
     * Registers a command for the reform script which will get executed in the interpreted version
     * of the script ({@link InterpretedReformScript})
     *
     * @param command The command which should be registered
     * @return The same reform script interpreter instance as used to call the method
     */
    @Nonnull
    ReformScriptInterpreter registerInterpreterCommand(@Nonnull InterpreterCommand command);

    /**
     * Registers a variable which should be replaced if the variable pattern appears in the script code
     *
     * @param variable The variable which should be registered
     * @return The same reform script interpreter instance as used to call the method
     */
    @Nonnull
    ReformScriptInterpreter registerInterpreterVariable(@Nonnull InterpreterVariable variable);

    /**
     * Get the command by the given name
     *
     * @param command The name of the command which should get found
     * @return The command by the given name or {@code null} if the command is unknown
     */
    @Nullable
    InterpreterCommand getCommand(@Nonnull String command);

    /**
     * Get the variable by the given name
     *
     * @param variable The name of the variable which should get found
     * @return The variable by the name or {@code null} if the variable is unknown
     */
    @Nullable
    InterpreterVariable getVariable(@Nonnull String variable);

    /**
     * Interprets the given file as a reform script
     *
     * @param script The file name of the file which should get interpreted
     * @return The interpreted script or {@code null} if the interpreter cannot understand the file content
     */
    @Nullable
    default InterpretedReformScript interpret(@Nonnull File script) {
        return this.interpret(script.toPath());
    }

    /**
     * Interprets the given path as a reform script
     *
     * @param script The path name of the file which should get interpreted
     * @return The interpreted script or {@code null} if the interpreter cannot understand the file content
     */
    @Nullable
    InterpretedReformScript interpret(@Nonnull Path script);

}
