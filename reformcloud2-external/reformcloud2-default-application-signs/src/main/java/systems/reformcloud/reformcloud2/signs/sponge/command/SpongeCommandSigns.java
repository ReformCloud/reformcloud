package systems.reformcloud.reformcloud2.signs.sponge.command;

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

import javax.annotation.Nonnull;
import java.util.Optional;

public class SpongeCommandSigns implements CommandExecutor {

    @Override
    @Nonnull
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {
        if (!(src instanceof Player)) {
            return CommandResult.success();
        }

        String type = args.<String>getOne("Execute type").orElse(null);
        String target = args.<String>getOne("Target group").orElse(null);

        if (type != null && target != null && type.equalsIgnoreCase("create")) {
            if (ExecutorAPI.getInstance().getProcessGroup(target) == null) {
                src.sendMessage(Text.of("§7The process group " + target + " does not exists"));
                return CommandResult.success();
            }

            Optional<BlockRayHit<World>> end = BlockRay.from((Player) src).distanceLimit(15).skipFilter(BlockRay.onlyAirFilter()).end();
            if (!end.isPresent() || !end.get().getLocation().getTileEntity().isPresent()
                    || !(end.get().getLocation().getTileEntity().get() instanceof Sign)) {
                src.sendMessage(Text.of("§cThe target Block is not a sign"));
                return CommandResult.success();
            }

            Sign sign = (Sign) end.get().getLocation().getTileEntity().get();

        }
        return CommandResult.success();
    }
}
