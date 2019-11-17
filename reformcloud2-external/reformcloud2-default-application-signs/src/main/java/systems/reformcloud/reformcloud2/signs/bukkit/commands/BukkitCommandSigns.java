package systems.reformcloud.reformcloud2.signs.bukkit.commands;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.signs.bukkit.adapter.BukkitSignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

import javax.annotation.Nonnull;

public class BukkitCommandSigns implements CommandExecutor {

    @Override
    public boolean onCommand(
            @Nonnull CommandSender commandSender,
            @Nonnull Command command,
            @Nonnull String s,
            @Nonnull String[] strings
    ) {
        if (!(commandSender.hasPermission("reformcloud.command.signs")) || !(commandSender instanceof Player)) {
            return true;
        }

        Player player = (Player) commandSender;

        if (strings.length == 2 && strings[0].equalsIgnoreCase("create")) {
            if (ExecutorAPI.getInstance().getProcessGroup(strings[1]) == null) {
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

        commandSender.sendMessage("§7/signs create [group]");
        commandSender.sendMessage("§7/signs delete");
        return true;
    }
}
