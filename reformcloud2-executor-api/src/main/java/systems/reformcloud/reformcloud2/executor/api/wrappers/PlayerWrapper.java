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
package systems.reformcloud.reformcloud2.executor.api.wrappers;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.task.Task;

import java.util.Optional;
import java.util.UUID;

public interface PlayerWrapper {

    @NotNull Task<Optional<ProcessInformation>> getConnectedProxy();

    @NotNull Task<Optional<ProcessInformation>> getConnectedServer();

    @NotNull Optional<UUID> getConnectedProxyUniqueId();

    @NotNull Optional<UUID> getConnectedServerUniqueId();

    void sendMessage(@NotNull String message);

    void disconnect(@NotNull String kickReason);

    void playSound(@NotNull String sound, float volume, float pitch);

    void sendTitle(@NotNull String title, @NotNull String subTitle, int fadeIn, int stay, int fadeOut);

    void playEffect(@NotNull String effect);

    void setLocation(@NotNull String world, double x, double y, double z, float yaw, float pitch);

    void connect(@NotNull String server);

    void connect(@NotNull UUID otherPlayer);
}
