package dk.martinersej.buycraft.commands.subbuyadmin;

import dk.martinersej.buycraft.BuyCraft;
import dk.martinersej.coinsystem.utils.command.CommandResult;
import dk.martinersej.coinsystem.utils.command.Result;
import dk.martinersej.coinsystem.utils.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class BuyAdminReloadCommand extends SubCommand {
    public BuyAdminReloadCommand(JavaPlugin plugin) {
        super(plugin, "reload buycraft", "reload", "buycraft.admin.reload", "reload", "rl");
    }

    public CommandResult execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender, getPermissions())) {
            return new CommandResult(this, Result.NO_PERMISSION);
        }
        BuyCraft.getConfigManager().load();
        BuyCraft.getMessageManager().load();
        BuyCraft.getBuycraftManager().load();
        sender.sendMessage("§aReloaded BuyCraft");
        return CommandResult.success(this);
    }
}