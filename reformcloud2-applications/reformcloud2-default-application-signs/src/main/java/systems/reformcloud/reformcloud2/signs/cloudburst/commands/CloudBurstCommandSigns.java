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
package systems.reformcloud.reformcloud2.signs.cloudburst.commands;

import com.nukkitx.protocol.bedrock.data.command.CommandParamType;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.blockentity.BlockEntity;
import org.cloudburstmc.server.blockentity.Sign;
import org.cloudburstmc.server.command.Command;
import org.cloudburstmc.server.command.CommandSender;
import org.cloudburstmc.server.command.data.CommandData;
import org.cloudburstmc.server.command.data.CommandParameter;
import org.cloudburstmc.server.player.Player;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.signs.cloudburst.adapter.CloudBurstSignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.SignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

public class CloudBurstCommandSigns extends Command {

    public CloudBurstCommandSigns() {
        super(CommandData.builder("signs")
            .setDescription("Manage signs")
            .addPermission("reformcloud.command.signs")
            .addParameters(new CommandParameter[]{
                new CommandParameter("create", CommandParamType.STRING, false),
                new CommandParameter("group", CommandParamType.STRING, false)
            })
            .addParameters(new CommandParameter[]{
                new CommandParameter("delete", CommandParamType.STRING, false)
            })
            .addParameters(new CommandParameter[]{
                new CommandParameter("deleteall", CommandParamType.STRING, false)
            })
            .addParameters(new CommandParameter[]{
                new CommandParameter("clean", CommandParamType.STRING, false)
            })
            .build());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] strings) {
        if (!(sender instanceof Player)) {
            return true;
        }

        CloudBurstSignSystemAdapter signSystemAdapter = CloudBurstSignSystemAdapter.getInstance();
        Player player = (Player) sender;

        if (strings.length == 2 && strings[0].equalsIgnoreCase("create")) {
            if (!ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroup(strings[1]).isPresent()) {
                sender.sendMessage("§7The process group " + strings[1] + " does not exists");
                return true;
            }

            Block block = player.getTargetBlock(15);
            if (block == null) {
                sender.sendMessage("§cThe target Block is not a sign");
                return true;
            }

            BlockEntity blockEntity = block.getLevel().getBlockEntity(block.getPosition());
            if (!(blockEntity instanceof Sign)) {
                sender.sendMessage("§cThe target Block is not a sign");
                return true;
            }

            Sign entitySign = (Sign) blockEntity;
            CloudSign cloudSign = signSystemAdapter.getSignAt(signSystemAdapter.getSignConverter().to(entitySign));
            if (cloudSign != null) {
                sender.sendMessage("§cThe sign already exists");
                return true;
            }

            signSystemAdapter.createSign(entitySign, strings[1]);
            sender.sendMessage("§7Created the sign successfully, please wait a second...");
            return true;
        }

        if (strings.length == 1 && strings[0].equalsIgnoreCase("delete")) {
            Block block = player.getTargetBlock(15);
            if (block == null) {
                sender.sendMessage("§cThe target Block is not a sign");
                return true;
            }

            BlockEntity blockEntity = block.getLevel().getBlockEntity(block.getPosition());
            if (!(blockEntity instanceof Sign)) {
                sender.sendMessage("§cThe target Block is not a sign");
                return true;
            }

            Sign sign = (Sign) blockEntity;
            CloudSign cloudSign = signSystemAdapter.getSignAt(signSystemAdapter.getSignConverter().to(sign));
            if (cloudSign == null) {
                sender.sendMessage("§cThe sign does not exists");
                return true;
            }

            signSystemAdapter.deleteSign(cloudSign.getLocation());
            sender.sendMessage("§7Deleted sign, please wait a second...");
            return true;
        }

        if (strings.length == 1 && strings[0].equalsIgnoreCase("deleteall")) {
            SignSystemAdapter.getInstance().deleteAll();
            sender.sendMessage("§7Deleting all signs, please wait...");
            return true;
        }

        if (strings.length == 1 && strings[0].equalsIgnoreCase("clean")) {
            SignSystemAdapter.getInstance().cleanSigns();
            sender.sendMessage("§7Cleaning signs, please wait...");
            return true;
        }

        sender.sendMessage("§7/signs create [group]");
        sender.sendMessage("§7/signs delete");
        sender.sendMessage("§7/signs deleteAll");
        sender.sendMessage("§7/signs clean");
        return true;
    }
}
