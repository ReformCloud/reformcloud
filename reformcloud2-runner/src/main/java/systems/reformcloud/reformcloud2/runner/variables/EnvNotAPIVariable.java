package systems.reformcloud.reformcloud2.runner.variables;

import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterVariable;

import javax.annotation.Nonnull;
import java.util.Collection;

public final class EnvNotAPIVariable extends InterpreterVariable {

    public EnvNotAPIVariable() {
        super("env_not_api");
    }

    @Nonnull
    @Override
    public String unwrap(@Nonnull String cursorLine, @Nonnull Collection<String> fullLines) {
        Integer integer = Integer.getInteger(cursorLine);
        return Boolean.toString(integer != null && integer != 3);
    }
}
