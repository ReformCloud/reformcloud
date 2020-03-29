package systems.reformcloud.reformcloud2.runner.variables;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterVariable;
import systems.reformcloud.reformcloud2.runner.util.RunnerUtils;

import java.nio.file.Files;
import java.util.Collection;

public final class SetupRequiredVariable extends InterpreterVariable {

    public SetupRequiredVariable() {
        super("setup_required");
    }

    @NotNull
    @Override
    public String unwrap(@NotNull String cursorLine, @NotNull Collection<String> fullLines) {
        Integer integer = Integer.getInteger("reformcloud.executor.type");
        return Boolean.toString((integer == null || integer != 3) || Files.notExists(RunnerUtils.EXECUTOR_PATH)
                || !RunnerUtils.GLOBAL_SCRIPT_FILE.exists());
    }
}
