package systems.reformcloud.reformcloud2.runner.reformscript.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Represents a variable in a reform script
 */
public abstract class InterpreterVariable {

    /**
     * Creates a new variable
     *
     * @param plain The plain variable name which can get wrapped
     */
    public InterpreterVariable(@NotNull String plain) {
        this.plain = plain.toLowerCase();
    }

    private final String plain;

    /**
     * @return The wrapped name of the variable
     */
    @NotNull
    public String wrap() {
        return "_%_" + this.plain + "_%_";
    }

    /**
     * Unwraps the current variable and returns the replaced string
     *
     * @param cursorLine The current line of the cursor
     * @param fullLines  All lines of the script
     * @return The replaced string of the current variable
     */
    @NotNull
    public abstract String unwrap(@NotNull String cursorLine, @NotNull Collection<String> fullLines);
}
