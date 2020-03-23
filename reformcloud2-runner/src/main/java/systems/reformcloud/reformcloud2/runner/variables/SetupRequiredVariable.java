package systems.reformcloud.reformcloud2.runner.variables;

import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterVariable;
import systems.reformcloud.reformcloud2.runner.util.RunnerUtils;

import javax.annotation.Nonnull;
import java.nio.file.Files;
import java.util.Collection;

public final class SetupRequiredVariable extends InterpreterVariable {

    public SetupRequiredVariable() {
        super("setup_required");
    }

    @Nonnull
    @Override
    public String unwrap(@Nonnull String cursorLine, @Nonnull Collection<String> fullLines) {
        return Boolean.toString(Files.notExists(RunnerUtils.EXECUTOR_PATH));
    }
}
