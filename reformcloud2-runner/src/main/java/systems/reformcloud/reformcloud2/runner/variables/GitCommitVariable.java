package systems.reformcloud.reformcloud2.runner.variables;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterVariable;

import java.util.Collection;

public final class GitCommitVariable extends InterpreterVariable {

    public GitCommitVariable() {
        super("git_commit");
    }

    @NotNull
    @Override
    public String unwrap(@NotNull String cursorLine, @NotNull Collection<String> fullLines) {
        return GitCommitVariable.class.getPackage().getSpecificationVersion();
    }
}
