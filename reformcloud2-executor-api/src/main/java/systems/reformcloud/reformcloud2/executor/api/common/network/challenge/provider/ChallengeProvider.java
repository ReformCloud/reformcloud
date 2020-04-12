package systems.reformcloud.reformcloud2.executor.api.common.network.challenge.provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ChallengeProvider {

    @Nullable
    byte[] createChallenge(@NotNull String sender);

    boolean checkResult(@NotNull String sender, @NotNull String challengeResult);
}
