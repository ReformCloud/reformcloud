/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.reformcloud.reformcloud2.signs.bukkit.commands;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.signs.bukkit.adapter.BukkitSignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.SignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

public class BukkitCommandSigns implements CommandExecutor {

    @Override
    public boolean onCommand(
        @NotNull CommandSender commandSender,
        @NotNull Command command,
        @NotNull String s,
        @NotNull String[] strings
    ) {
        if (!(commandSender.hasPermission("reformcloud.command.signs")) || !(commandSender instanceof Player)) {
            return true;
        }

        Player player = (Player) commandSender;
        if (strings.length == 2 && strings[0].equalsIgnoreCase("create")) {
            if (!ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroup(strings[1]).isPresent()) {
                commandSender.sendMessage("§7The process group " + strings[1] + " does not exists");
                return true;
            }

            Block block = player.getTargetBlock(null, 15);
            if (block.getState() instanceof Sign) {
                Sign sign = (Sign) block.getState();

                CloudSign cloudSign = BukkitSignSystemAdapter.getInstance().getSignAt(
                    BukkitSignSystemAdapter.getInstance().getSignConverter().to(sign)
                );
                if (cloudSign != null) {
                    commandSender.sendMessage("§cThe sign already exists");
                    return true;
                }

                BukkitSignSystemAdapter.getInstance().createSign(sign, strings[1]);
                commandSender.sendMessage("§7Created the sign successfully, please wait a second...");
                return true;
            }

            commandSender.sendMessage("§cThe target Block is not a sign");
            return true;
        }

        if (strings.length == 1 && strings[0].equalsIgnoreCase("delete")) {
            Block block = player.getTargetBlock(null, 15);
            if (block.getState() instanceof Sign) {
                Sign sign = (Sign) block.getState();
                CloudSign cloudSign = BukkitSignSystemAdapter.getInstance().getSignAt(
                    BukkitSignSystemAdapter.getInstance().getSignConverter().to(sign)
                );

                if (cloudSign == null) {
                    commandSender.sendMessage("§cThe sign does not exists");
                    return true;
                }

                BukkitSignSystemAdapter.getInstance().deleteSign(cloudSign.getLocation());
                commandSender.sendMessage("§7Deleted sign, please wait a second...");
                return true;
            }
        }

        if (strings.length == 1 && strings[0].equalsIgnoreCase("deleteall")) {
            SignSystemAdapter.getInstance().deleteAll();
            commandSender.sendMessage("§7Deleting all signs, please wait...");
            return true;
        }

        if (strings.length == 1 && strings[0].equalsIgnoreCase("clean")) {
            SignSystemAdapter.getInstance().cleanSigns();
            commandSender.sendMessage("§7Cleaning signs, please wait...");
            return true;
        }

        commandSender.sendMessage("§7/signs create [group]");
        commandSender.sendMessage("§7/signs delete");
        commandSender.sendMessage("§7/signs deleteAll");
        commandSender.sendMessage("§7/signs clean");
        return true;
    }
}
