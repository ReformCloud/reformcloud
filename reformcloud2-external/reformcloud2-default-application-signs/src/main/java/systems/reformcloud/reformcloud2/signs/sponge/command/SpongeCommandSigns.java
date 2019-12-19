package systems.reformcloud.reformcloud2.signs.sponge.command;

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
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.signs.sponge.adapter.SpongeSignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

import javax.annotation.Nonnull;
import java.util.Optional;

public class SpongeCommandSigns implements CommandExecutor {

    @Override
    @Nonnull
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {
        if (!(src instanceof Player)) {
            return CommandResult.success();
        }

        Player player = (Player) src;

        String type = args.<String>getOne("Execute type").orElse(null);
        String target = args.<String>getOne("Target group").orElse(null);

        if (type != null && target != null && type.equalsIgnoreCase("create")) {
            if (ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroup(target) == null) {
                src.sendMessage(Text.of("§7The process group " + target + " does not exists"));
                return CommandResult.success();
            }

            Optional<BlockRayHit<World>> end = getSign(player);
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
            Optional<BlockRayHit<World>> end = getSign(player);
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

        src.sendMessage(Text.of("§7/signs create [group]"));
        src.sendMessage(Text.of("§7/signs delete"));
        return CommandResult.success();
    }

    private Optional<BlockRayHit<World>> getSign(Player player) {
        return BlockRay
                .from(player)
                .distanceLimit(15)
                .narrowPhase(true)
                .stopFilter(lastHit -> {
                    final BlockType blockType = lastHit.getExtent().getBlockType(lastHit.getBlockX(), lastHit.getBlockY(), lastHit.getBlockZ());
                    return blockType.equals(BlockTypes.STANDING_SIGN) || blockType.equals(BlockTypes.WALL_SIGN);
                })
                .end();
    }
}
