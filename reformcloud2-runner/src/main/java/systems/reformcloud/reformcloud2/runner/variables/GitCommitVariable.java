package systems.reformcloud.reformcloud2.runner.variables;

import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterVariable;

import javax.annotation.Nonnull;
import java.util.Collection;

public final class GitCommitVariable extends InterpreterVariable {

    public GitCommitVariable() {
        super("git_commit");
    }

    @Nonnull
    @Override
    public String unwrap(@Nonnull String cursorLine, @Nonnull Collection<String> fullLines) {
        return GitCommitVariable.class.getPackage().getSpecificationVersion();
    }
}
