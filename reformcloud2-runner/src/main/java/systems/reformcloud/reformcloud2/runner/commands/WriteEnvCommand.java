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
package systems.reformcloud.reformcloud2.runner.commands;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.runner.reformscript.InterpretedReformScript;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterCommand;
import systems.reformcloud.reformcloud2.runner.util.RunnerUtils;

import java.util.Collection;

public final class WriteEnvCommand extends InterpreterCommand {

    public WriteEnvCommand() {
        super("write_env");
    }

    @Override
    public void execute(@NotNull String cursorLine, @NotNull InterpretedReformScript script, @NotNull Collection<String> allLines) {
        String executorID = System.getProperty("reformcloud.executor.type");
        if (executorID == null) {
            throw new IllegalArgumentException("Unable to write executor id which is not specified");
        }

        String version = WriteEnvCommand.class.getPackage().getImplementationVersion();
        System.setProperty("reformcloud.runner.version", version);

        RunnerUtils.rewriteFile(script.getScriptPath(), s -> {
            if (s.startsWith("# VARIABLE reformcloud.executor.type=") || s.startsWith("VARIABLE reformcloud.executor.type=")) {
                return "VARIABLE reformcloud.executor.type=" + executorID;
            }

            if (s.startsWith("# VARIABLE reformcloud.runner.version=") || s.startsWith("VARIABLE reformcloud.runner.version=")) {
                s = "VARIABLE reformcloud.runner.version=" + version;
            }

            return s;
        });
    }
}
