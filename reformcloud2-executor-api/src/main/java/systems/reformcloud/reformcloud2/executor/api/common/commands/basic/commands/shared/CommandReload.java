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
package systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.shared;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.runtime.ReloadableRuntime;

import java.util.Collections;

public final class CommandReload extends GlobalCommand {

    private final ReloadableRuntime reloadableRuntime;

    public CommandReload(ReloadableRuntime reloadableRuntime) {
        super("reload", null, GlobalCommand.DEFAULT_DESCRIPTION, Collections.singletonList(
                "rl"
        ));

        this.reloadableRuntime = reloadableRuntime;
    }

    @Override
    public void describeCommandToSender(@NotNull CommandSource source) {
        source.sendMessage(LanguageManager.get("command-reload-description"));
    }

    @Override
    public boolean handleCommand(@NotNull CommandSource commandSource, @NotNull String[] strings) {
        try {
            reloadableRuntime.reload();
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }
}
