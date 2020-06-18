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
package systems.reformcloud.reformcloud2.node.commands;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.command.Command;
import systems.reformcloud.reformcloud2.executor.api.command.CommandSender;
import systems.reformcloud.reformcloud2.node.NodeExecutor;
import systems.reformcloud.reformcloud2.node.tick.TickAverageCounter;

public final class CommandTicksPerSecond implements Command {

    @Override
    public void process(@NotNull CommandSender sender, String[] strings, @NotNull String commandLine) {
        TickAverageCounter one = NodeExecutor.getInstance().getCloudTickWorker().getTps1();
        TickAverageCounter five = NodeExecutor.getInstance().getCloudTickWorker().getTps5();
        TickAverageCounter fifteen = NodeExecutor.getInstance().getCloudTickWorker().getTps15();

        sender.sendMessage("TPS from last 1m, 5m, 15m: "
                + format(one.getAverage()) + ", " + format(five.getAverage()) + ", " + format(fifteen.getAverage()));
    }

    @NotNull
    private static String format(double tps) {
        return ((tps > 18.0) ? "&a" : (tps > 16.0) ? "&e" : "&c") + ((tps > 20.0) ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
    }
}
