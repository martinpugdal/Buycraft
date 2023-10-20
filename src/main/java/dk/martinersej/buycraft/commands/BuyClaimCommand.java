package dk.martinersej.buycraft.commands;

import dk.martinersej.buycraft.BuyCraft;
import dk.martinersej.coinsystem.utils.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class BuyClaimCommand extends Command implements CommandExecutor {

    public BuyClaimCommand(JavaPlugin plugin) {
        super(plugin);
        plugin.getCommand("buyclaim").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        BuyCraft.getBuycraftManager().openBoughtItemsMenu((org.bukkit.entity.Player) commandSender);
        return true;
    }
}
