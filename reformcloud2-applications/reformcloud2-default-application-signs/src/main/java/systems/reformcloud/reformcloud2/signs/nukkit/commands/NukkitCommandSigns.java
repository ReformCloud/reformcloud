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
package systems.reformcloud.reformcloud2.signs.nukkit.commands;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.Sign;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.player.Player;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.signs.nukkit.adapter.NukkitSignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.SignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

public class NukkitCommandSigns implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender.hasPermission("reformcloud.command.signs")) || !(commandSender instanceof Player)) {
            return true;
        }

        NukkitSignSystemAdapter signSystemAdapter = NukkitSignSystemAdapter.getInstance();
        Player player = (Player) commandSender;

        if (strings.length == 2 && strings[0].equalsIgnoreCase("create")) {
            if (!ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroup(strings[1]).isPresent()) {
                commandSender.sendMessage("§7The process group " + strings[1] + " does not exists");
                return true;
            }

            Block block = player.getTargetBlock(15);
            if (block == null || !(block.getLevel().getBlockEntity(block.getPosition()) instanceof Sign)) {
                commandSender.sendMessage("§cThe target Block is not a sign");
                return true;
            }

            Sign entitySign = (Sign) block.getLevel().getBlockEntity(block.getPosition());
            CloudSign cloudSign = signSystemAdapter.getSignAt(signSystemAdapter.getSignConverter().to(entitySign));
            if (cloudSign != null) {
                commandSender.sendMessage("§cThe sign already exists");
                return true;
            }

            signSystemAdapter.createSign(entitySign, strings[1]);
            commandSender.sendMessage("§7Created the sign successfully, please wait a second...");
            return true;
        }

        if (strings.length == 1 && strings[0].equalsIgnoreCase("delete")) {
            Block block = player.getTargetBlock(15);
            if (block == null || !(block.getLevel().getBlockEntity(block.getPosition()) instanceof Sign)) {
                commandSender.sendMessage("§cThe target Block is not a sign");
                return true;
            }

            Sign entitySign = (Sign) block.getLevel().getBlockEntity(block.getPosition());
            CloudSign cloudSign = signSystemAdapter.getSignAt(signSystemAdapter.getSignConverter().to(entitySign));
            if (cloudSign == null) {
                commandSender.sendMessage("§cThe sign does not exists");
                return true;
            }

            signSystemAdapter.deleteSign(cloudSign.getLocation());
            commandSender.sendMessage("§7Deleted sign, please wait a second...");
            return true;
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
