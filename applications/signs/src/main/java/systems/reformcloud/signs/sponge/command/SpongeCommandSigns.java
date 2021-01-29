/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
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
package systems.reformcloud.signs.sponge.command;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.signs.sponge.adapter.SpongeSignSystemAdapter;
import systems.reformcloud.signs.util.SignSystemAdapter;
import systems.reformcloud.signs.util.sign.CloudSign;

import java.util.Optional;

public class SpongeCommandSigns implements CommandExecutor {

  @Override
  @NotNull
  public CommandResult execute(@NotNull CommandSource src, @NotNull CommandContext args) {
    if (!(src instanceof Player)) {
      return CommandResult.success();
    }

    Player player = (Player) src;

    String type = args.<String>getOne("Execute type").orElse(null);
    String target = args.<String>getOne("Target group").orElse(null);

    if (type != null && target != null && type.equalsIgnoreCase("create")) {
      if (!ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroup(target).isPresent()) {
        src.sendMessage(Text.of("§7The process group " + target + " does not exists"));
        return CommandResult.success();
      }

      Optional<BlockRayHit<World>> end = this.getSign(player);
      if (!end.isPresent() || !(end.get().getLocation().getTileEntity().orElse(null) instanceof Sign)) {
        src.sendMessage(Text.of("§cThe target Block is not a sign, try step one block back"));
        return CommandResult.success();
      }

      Sign sign = (Sign) end.get().getLocation().getTileEntity().get();
      CloudSign cloudSign = SpongeSignSystemAdapter.getInstance().getSignAt(
        SpongeSignSystemAdapter.getInstance().getSignConverter().to(sign)
      );
      if (cloudSign != null) {
        src.sendMessage(Text.of("§cThe sign already exists"));
        return CommandResult.success();
      }

      SpongeSignSystemAdapter.getInstance().createSign(sign, target);
      src.sendMessage(Text.of("§7Created the sign successfully, please wait a second..."));
      return CommandResult.success();
    }

    if (type != null && type.equalsIgnoreCase("delete")) {
      Optional<BlockRayHit<World>> end = this.getSign(player);
      if (!end.isPresent() || !(end.get().getLocation().getTileEntity().orElse(null) instanceof Sign)) {
        src.sendMessage(Text.of("§cThe target Block is not a sign, try step one block back"));
        return CommandResult.success();
      }

      Sign sign = (Sign) end.get().getLocation().getTileEntity().get();
      CloudSign cloudSign = SpongeSignSystemAdapter.getInstance().getSignAt(
        SpongeSignSystemAdapter.getInstance().getSignConverter().to(sign)
      );
      if (cloudSign == null) {
        src.sendMessage(Text.of("§cThe sign does not exists"));
        return CommandResult.success();
      }

      SpongeSignSystemAdapter.getInstance().deleteSign(cloudSign.getLocation());
      src.sendMessage(Text.of("§7Deleted the sign successfully, please wait a second..."));
      return CommandResult.success();
    }

    if (type != null && type.equalsIgnoreCase("deleteall")) {
      SignSystemAdapter.getInstance().deleteAll();
      src.sendMessage(Text.of("§7Deleting all signs, please wait..."));
      return CommandResult.success();
    }

    if (type != null && type.equalsIgnoreCase("clean")) {
      SignSystemAdapter.getInstance().cleanSigns();
      src.sendMessage(Text.of("§7Cleaning signs, please wait..."));
      return CommandResult.success();
    }

    src.sendMessage(Text.of("§7/signs create [group]"));
    src.sendMessage(Text.of("§7/signs delete"));
    src.sendMessage(Text.of("§7/signs deleteAll"));
    src.sendMessage(Text.of("§7/signs clean"));
    return CommandResult.success();
  }

  private Optional<BlockRayHit<World>> getSign(Player player) {
    return BlockRay
      .from(player)
      .distanceLimit(15)
      .narrowPhase(true)
      .select(BlockRay.notAirFilter())
      .whilst(lastHit -> {
        final BlockType blockType = lastHit.getExtent().getBlockType(lastHit.getBlockX(), lastHit.getBlockY(), lastHit.getBlockZ());
        return blockType.equals(BlockTypes.STANDING_SIGN) || blockType.equals(BlockTypes.WALL_SIGN);
      })
      .end();
  }
}
