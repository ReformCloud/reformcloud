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
package systems.reformcloud.reformcloud2.signs.gomint.commands;

import io.gomint.command.Command;
import io.gomint.command.CommandOutput;
import io.gomint.command.CommandSender;
import io.gomint.command.PlayerCommandSender;
import io.gomint.command.validator.StringValidator;
import io.gomint.entity.EntityPlayer;
import io.gomint.math.Vector;
import io.gomint.world.block.Block;
import io.gomint.world.block.BlockSign;
import io.gomint.world.block.BlockType;
import io.gomint.world.block.data.Facing;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.signs.gomint.adapter.GoMintSignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.SignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

import java.util.Map;
import java.util.Optional;

public class GoMintCommandSigns extends Command {

    private final GoMintSignSystemAdapter goMintAdapter;

    public GoMintCommandSigns(GoMintSignSystemAdapter goMintAdapter) {
        super("signs");

        // Initial values setup
        super.description("Create and manage reform cloud signs");
        super.permission("reformcloud.command.signs");

        // Parameter setup
        super.overload().param("clean", new StringValidator("(?i)[clean]+"));
        super.overload().param("delete", new StringValidator("(?i)[delete]+"));
        super.overload().param("deleteall", new StringValidator("(?i)[deleteall]+"));
        super.overload()
            .param("create", new StringValidator("(?i)[create]+"))
            .param("group", new StringValidator(".*"));

        this.goMintAdapter = goMintAdapter;
    }

    @Override
    public CommandOutput execute(CommandSender commandSender, String s, Map<String, Object> map) {
        if (!(commandSender instanceof PlayerCommandSender)) {
            return CommandOutput.failure("Command is only executable as player");
        }

        EntityPlayer sender = (EntityPlayer) commandSender;
        if (map.containsKey("clean")) {
            SignSystemAdapter.getInstance().cleanSigns();
            return CommandOutput.successful("§7Cleaning signs, please wait...");
        }

        if (map.containsKey("deleteall")) {
            SignSystemAdapter.getInstance().deleteAll();
            return CommandOutput.successful("§7Deleting all signs, please wait...");
        }

        if (map.containsKey("delete")) {
            Optional<BlockSign> lookingBlock = getTargetBlockSign(sender);
            if (!lookingBlock.isPresent()) {
                return CommandOutput.failure("§7You aren't looking at a sign!");
            }

            CloudSign target = this.goMintAdapter.getSignAt(this.goMintAdapter.getSignConverter().to(lookingBlock.get()));
            if (target == null) {
                return CommandOutput.failure("§7You aren't looking at a cloud sign!");
            }

            this.goMintAdapter.deleteSign(target.getLocation());
            return CommandOutput.successful("§7Deleted sign, please wait a second...");
        }

        if (map.containsKey("create")) {
            String targetGroup = (String) map.get("group");
            if (targetGroup == null || !ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroup(targetGroup).isPresent()) {
                return CommandOutput.failure("§7The process group %s does not exists", targetGroup);
            }

            Optional<BlockSign> lookingBlock = getTargetBlockSign(sender);
            if (!lookingBlock.isPresent()) {
                return CommandOutput.failure("§7You aren't looking at a sign!");
            }

            CloudSign target = this.goMintAdapter.getSignAt(this.goMintAdapter.getSignConverter().to(lookingBlock.get()));
            if (target != null) {
                return CommandOutput.failure("§7The target sign already exists");
            }

            this.goMintAdapter.createSign(lookingBlock.get(), targetGroup);
            return CommandOutput.successful("§7Created the sign successfully, please wait a second...");
        }

        return CommandOutput.failure();
    }

    private static Optional<BlockSign> getTargetBlockSign(@NotNull EntityPlayer entityPlayer) {
        Facing facing = getPlayerFacing(entityPlayer);
        Vector start = new Vector(entityPlayer.getLocation().getX(), entityPlayer.getLocation().getY() + entityPlayer.getEyeHeight(), entityPlayer.getLocation().getZ());

        Block current = entityPlayer.getWorld().getBlockAt(start.toBlockPosition());
        if (current.getBlockType() == BlockType.AIR) {
            for (int i = 0; i < 5; i++) {
                current = current.getSide(facing);
                entityPlayer.sendMessage(current.getBlockType().name());
                if (current.getBlockType() != BlockType.AIR) {
                    break;
                }
            }
        }

        return current instanceof BlockSign ? Optional.of((BlockSign) current) : Optional.empty();
    }

    @Nullable
    private static Facing getPlayerFacing(@NotNull EntityPlayer entityPlayer) {
        double rotation = entityPlayer.getLocation().getYaw() % 360.0D;
        if (rotation < 0.0D) {
            rotation += 360.0D;
        }

        if ((0.0D > rotation || rotation >= 45.0D) && (315.0D > rotation || rotation >= 360.0D)) {
            if (45.0D <= rotation && rotation < 135.0D) {
                return Facing.WEST;
            } else if (135.0D <= rotation && rotation < 225.0D) {
                return Facing.NORTH;
            } else {
                return 225.0D <= rotation && rotation < 315.0D ? Facing.EAST : null;
            }
        }

        return Facing.SOUTH;
    }
}
