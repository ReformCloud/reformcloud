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
package systems.reformcloud.reformcloud2.shared.command.sources;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.command.CommandSender;

public final class ConsoleCommandSender implements CommandSender {

    public static final CommandSender INSTANCE = new ConsoleCommandSender();

    private ConsoleCommandSender() {
    }

    @Override
    public void sendMessage(@NotNull String message) {
        System.out.println(message);
    }

    @Override
    public void sendMessages(@NonNls String[] messages) {
        for (String message : messages) {
            System.out.println(message);
        }
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return true;
    }

    @NotNull
    @Override
    public String getName() {
        return ConsoleCommandSender.class.getSimpleName();
    }
}
