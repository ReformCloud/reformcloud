/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.executor.api.common.network.challenge.shared;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.provider.ChallengeProvider;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.security.ChallengeSecurity;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class SharedChallengeProvider implements ChallengeProvider {

    private final String authKey;
    private final Map<String, String> runningChallenges = new ConcurrentHashMap<>();

    public SharedChallengeProvider(String authKey) {
        this.authKey = authKey;
    }

    @Nullable
    @Override
    public byte[] createChallenge(@NotNull String sender) {
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
    public boolean checkResult(@NotNull String sender, @NotNull String challengeResult) {
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
