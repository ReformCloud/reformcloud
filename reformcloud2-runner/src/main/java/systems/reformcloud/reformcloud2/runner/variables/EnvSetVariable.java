package systems.reformcloud.reformcloud2.runner.variables;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterVariable;

import java.util.Collection;

public final class EnvSetVariable extends InterpreterVariable {

    public EnvSetVariable() {
        super("env_set");
    }

    @NotNull
    @Override
    public String unwrap(@NotNull String cursorLine, @NotNull Collection<String> fullLines) {
        Integer integer = Integer.getInteger("reformcloud.executor.type");
        return Boolean.toString(integer != null && integer > 0 && integer < 5);
    }
}
