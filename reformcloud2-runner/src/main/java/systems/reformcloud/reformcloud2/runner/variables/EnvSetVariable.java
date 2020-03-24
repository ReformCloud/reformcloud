package systems.reformcloud.reformcloud2.runner.variables;

import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterVariable;

import javax.annotation.Nonnull;
import java.util.Collection;

public final class EnvSetVariable extends InterpreterVariable {

    public EnvSetVariable() {
        super("env_set");
    }

    @Nonnull
    @Override
    public String unwrap(@Nonnull String cursorLine, @Nonnull Collection<String> fullLines) {
        Integer integer = Integer.getInteger("reformcloud.executor.type");
        return Boolean.toString(integer != null && integer > 0 && integer < 5);
    }
}
