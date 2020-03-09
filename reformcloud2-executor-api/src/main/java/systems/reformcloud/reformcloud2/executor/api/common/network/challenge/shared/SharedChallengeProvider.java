package systems.reformcloud.reformcloud2.executor.api.common.network.challenge.shared;

import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.provider.ChallengeProvider;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.security.ChallengeSecurity;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class SharedChallengeProvider implements ChallengeProvider {

    public SharedChallengeProvider(String authKey) {
        this.authKey = authKey;
    }

    private final String authKey;

    private final Map<String, String> runningChallenges = new ConcurrentHashMap<>();

    @Nullable
    @Override
    public byte[] createChallenge(@Nonnull String sender) {
        String challenge = StringUtil.generateString(5);
        String hashedChallenge = ChallengeSecurity.hash(challenge);
        byte[] result = ChallengeSecurity.encryptChallengeRequest(this.authKey, challenge);

        if (hashedChallenge == null || result == null) {
            return null;
        }

        runningChallenges.put(sender, hashedChallenge);
        this.removeLater(sender);
        return result;
    }

    @Override
    public boolean checkResult(@Nonnull String sender, @Nonnull String challengeResult) {
        String expected = runningChallenges.get(sender);
        return expected != null && expected.equals(challengeResult);
    }

    private void removeLater(String sender) {
        Task.EXECUTOR.execute(() -> {
            AbsoluteThread.sleep(TimeUnit.SECONDS, 30);
            this.runningChallenges.remove(sender);
        });
    }
}
