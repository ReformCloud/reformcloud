package systems.reformcloud.reformcloud2.runner.variables;

import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterVariable;
import systems.reformcloud.reformcloud2.runner.util.RunnerUtils;

import javax.annotation.Nonnull;
import java.util.Collection;

public final class GithubUrlVariable extends InterpreterVariable {

    public GithubUrlVariable() {
        super("github_url");
    }

    @Nonnull
    @Override
    public String unwrap(@Nonnull String cursorLine, @Nonnull Collection<String> fullLines) {
        return RunnerUtils.REPO_BASE_URL;
    }
}
