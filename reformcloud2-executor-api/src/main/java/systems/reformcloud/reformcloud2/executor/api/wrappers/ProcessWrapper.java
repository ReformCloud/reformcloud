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
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.task.Task;

import java.util.Optional;
import java.util.Queue;

public interface ProcessWrapper {

    @NotNull
    ProcessInformation getProcessInformation();

    @NotNull
    Optional<ProcessInformation> requestProcessInformation();

    @NotNull
    Optional<ProcessInformation> requestProcessInformationUpdate();

    @NotNull
    Optional<String> uploadLog();

    @NotNull
    @UnmodifiableView Queue<String> getLastLogLines();

    void start();

    void stop();

    void restart();

    void stopAndDelete();

    void reset();

    void copy(@NotNull Template template);

    void copy(@NotNull String path);

    void copy(@NotNull String templateGroup, @NotNull String templateName);

    void copy(@NotNull String templateGroup, @NotNull String templateName, @NotNull String templateBackend);

    @NotNull
    default Task<ProcessInformation> getProcessInformationAsync() {
        return Task.supply(this::getProcessInformation);
    }

    @NotNull
    default Task<Optional<ProcessInformation>> requestProcessInformationAsync() {
        return Task.supply(this::requestProcessInformation);
    }

    @NotNull
    default Task<Optional<ProcessInformation>> requestProcessInformationUpdateAsync() {
        return Task.supply(this::requestProcessInformationUpdate);
    }

    @NotNull
    default Task<Optional<String>> uploadLogAsync() {
        return Task.supply(this::uploadLog);
    }

    @NotNull
    default Task<Queue<String>> getLastLogLinesAsync() {
        return Task.supply(this::getLastLogLines);
    }

    @NotNull
    default Task<Void> startAsync() {
        return Task.supply(() -> {
            this.start();
            return null;
        });
    }

    @NotNull
    default Task<Void> stopAsync() {
        return Task.supply(() -> {
            this.stop();
            return null;
        });
    }

    @NotNull
    default Task<Void> restartAsync() {
        return Task.supply(() -> {
            this.restart();
            return null;
        });
    }

    @NotNull
    default Task<Void> stopAndDeleteAsync() {
        return Task.supply(() -> {
            this.stopAndDelete();
            return null;
        });
    }

    @NotNull
    default Task<Void> resetAsync() {
        return Task.supply(() -> {
            this.reset();
            return null;
        });
    }

    @NotNull
    default Task<Void> copyAsync(@NotNull Template template) {
        return Task.supply(() -> {
            this.copy(template);
            return null;
        });
    }

    @NotNull
    default Task<Void> copyAsync(@NotNull String path) {
        return Task.supply(() -> {
            this.copy(path);
            return null;
        });
    }

    @NotNull
    default Task<Void> copyAsync(@NotNull String templateGroup, @NotNull String templateName) {
        return Task.supply(() -> {
            this.copy(templateGroup, templateName);
            return null;
        });
    }

    @NotNull
    default Task<Void> copyAsync(@NotNull String templateGroup, @NotNull String templateName, @NotNull String templateBackend) {
        return Task.supply(() -> {
            this.copy(templateGroup, templateName, templateBackend);
            return null;
        });
    }
}
