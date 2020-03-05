package systems.reformcloud.reformcloud2.executor.api.common.network.challenge.provider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ChallengeProvider {

    @Nullable
    byte[] createChallenge(@Nonnull String sender);

    boolean checkResult(@Nonnull String sender, @Nonnull String challengeResult);
}
