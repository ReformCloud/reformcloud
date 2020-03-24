package systems.reformcloud.reformcloud2.runner.reformscript.utils;

import javax.annotation.Nonnull;
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
    public InterpreterVariable(@Nonnull String plain) {
        this.plain = plain.toLowerCase();
    }

    private final String plain;

    /**
     * @return The wrapped name of the variable
     */
    @Nonnull
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
    @Nonnull
    public abstract String unwrap(@Nonnull String cursorLine, @Nonnull Collection<String> fullLines);
}
