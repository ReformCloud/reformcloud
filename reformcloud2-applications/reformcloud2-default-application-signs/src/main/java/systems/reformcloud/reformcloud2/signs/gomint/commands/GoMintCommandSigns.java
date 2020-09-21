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
package systems.reformcloud.reformcloud2.signs.gomint.commands;

import io.gomint.command.Command;
import io.gomint.command.CommandOutput;
import io.gomint.command.CommandSender;
import io.gomint.command.PlayerCommandSender;
import io.gomint.command.annotation.*;
import io.gomint.command.validator.StringValidator;
import io.gomint.entity.EntityPlayer;
import io.gomint.math.Vector;
import io.gomint.world.block.Block;
import io.gomint.world.block.BlockSign;
import io.gomint.world.block.BlockType;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.signs.gomint.adapter.GoMintSignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.SignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Name("signs")
@Permission("reformcloud.command.signs")
@Description("Create and manage reform cloud signs")
@Overload(@Parameter(name = "clean", validator = StringValidator.class, arguments = "(?i)[clean]+"))
@Overload(@Parameter(name = "delete", validator = StringValidator.class, arguments = "(?i)[delete]+"))
@Overload(@Parameter(name = "deleteall", validator = StringValidator.class, arguments = "(?i)[deleteall]+"))
@Overload({
    @Parameter(name = "create", validator = StringValidator.class, arguments = "(?i)[create]+"),
    @Parameter(name = "group", validator = StringValidator.class, arguments = ".*")
})
public class GoMintCommandSigns extends Command {

    private final GoMintSignSystemAdapter goMintAdapter;

    public GoMintCommandSigns(GoMintSignSystemAdapter goMintAdapter) {
        this.goMintAdapter = goMintAdapter;
    }

    @Override
    public CommandOutput execute(CommandSender commandSender, String s, Map<String, Object> map) {
        if (!(commandSender instanceof PlayerCommandSender)) {
            return new CommandOutput().fail("Command is only executable as player");
        }

        EntityPlayer sender = (EntityPlayer) commandSender;
        if (map.containsKey("clean")) {
            SignSystemAdapter.getInstance().cleanSigns();
            return new CommandOutput().success("§7Cleaning signs, please wait...");
        }

        if (map.containsKey("deleteall")) {
            SignSystemAdapter.getInstance().deleteAll();
            return new CommandOutput().success("§7Deleting all signs, please wait...");
        }

        if (map.containsKey("delete")) {
            Optional<BlockSign> lookingBlock = getTargetBlockSign(sender);
            if (!lookingBlock.isPresent()) {
                return new CommandOutput().fail("§7You aren't looking at a sign!");
            }

            CloudSign target = this.goMintAdapter.getSignAt(this.goMintAdapter.getSignConverter().to(lookingBlock.get()));
            if (target == null) {
                return new CommandOutput().fail("§7You aren't looking at a cloud sign!");
            }

            this.goMintAdapter.deleteSign(target.getLocation());
            return new CommandOutput().success("§7Deleted sign, please wait a second...");
        }

        if (map.containsKey("create")) {
            String targetGroup = (String) map.get("group");
            if (targetGroup == null || !ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroup(targetGroup).isPresent()) {
                return new CommandOutput().fail("§7The process group %s does not exists", targetGroup);
            }

            Optional<BlockSign> lookingBlock = getTargetBlockSign(sender);
            if (!lookingBlock.isPresent()) {
                return new CommandOutput().fail("§7You aren't looking at a sign!");
            }

            CloudSign target = this.goMintAdapter.getSignAt(this.goMintAdapter.getSignConverter().to(lookingBlock.get()));
            if (target != null) {
                return new CommandOutput().fail("§7The target sign already exists");
            }

            this.goMintAdapter.createSign(lookingBlock.get(), targetGroup);
            return new CommandOutput().success("§7Created the sign successfully, please wait a second...");
        }

        return new CommandOutput();
    }

    private static Optional<BlockSign> getTargetBlockSign(@NotNull EntityPlayer entityPlayer) {
        InternalFacing facing = InternalFacing.fromPlayer(entityPlayer);
        Vector start = new Vector(entityPlayer.getLocation().getX(), entityPlayer.getEyeHeight(), entityPlayer.getLocation().getZ());

        Block current = entityPlayer.getWorld().getBlockAt(start.toBlockPosition());
        if (current.getBlockType() == BlockType.AIR) {
            for (int i = 0; i < 5; i++) {
                facing.stepper.accept(start); // Step in players facing direction
                current = entityPlayer.getWorld().getBlockAt(start.toBlockPosition());
                if (current.getBlockType() != BlockType.AIR) {
                    break;
                }
            }
        }

        return current instanceof BlockSign ? Optional.of((BlockSign) current) : Optional.empty();
    }

    private enum InternalFacing {

        SOUTH(vector -> vector.add(0, 0, 1)),
        WEST(vector -> vector.subtract(1, 0, 0)),
        EAST(vector -> vector.add(1, 0, 0)),
        NORTH(vector -> vector.subtract(0, 0, 1));

        private final Consumer<Vector> stepper;

        InternalFacing(Consumer<Vector> stepper) {
            this.stepper = stepper;
        }

        public static InternalFacing fromPlayer(@NotNull EntityPlayer entityPlayer) {
            float yaw = entityPlayer.getLocation().getYaw();
            yaw = (yaw % 360 + 360) % 360;
            if (yaw > 135 || yaw < -135) {
                return InternalFacing.NORTH;
            } else if (yaw < -45) {
                return InternalFacing.EAST;
            } else if (yaw > 45) {
                return InternalFacing.WEST;
            } else {
                return InternalFacing.SOUTH;
            }
        }
    }
}
