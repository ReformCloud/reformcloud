package systems.reformcloud.reformcloud2.runner.variables;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterVariable;

import java.util.Collection;

public final class EnvNotAPIVariable extends InterpreterVariable {

    public EnvNotAPIVariable() {
        super("env_not_api");
    }

    @NotNull
    @Override
    public String unwrap(@NotNull String cursorLine, @NotNull Collection<String> fullLines) {
        Integer integer = Integer.getInteger(cursorLine);
        return Boolean.toString(integer != null && integer != 3);
    }
}
