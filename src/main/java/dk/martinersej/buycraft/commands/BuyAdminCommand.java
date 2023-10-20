package dk.martinersej.buycraft.commands;

import dk.martinersej.buycraft.commands.subbuyadmin.BuyAdminReloadCommand;
import dk.martinersej.coinsystem.utils.command.Command;
import dk.martinersej.coinsystem.utils.command.CommandResult;
import dk.martinersej.coinsystem.utils.command.SubCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

public class BuyAdminCommand extends Command implements CommandExecutor, TabCompleter {

    public BuyAdminCommand(JavaPlugin plugin) {
        super(plugin);
        PluginCommand command = plugin.getCommand("buyadmin");
        command.setExecutor(this);
        command.setTabCompleter(this);
        command.setAliases(Collections.singletonList("badmin"));

        super.addSubCommand(new BuyAdminReloadCommand(plugin));
    }


    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String label, String[] args) {
        CommandResult result = super.execute(commandSender, args);
        switch (result.getResult()) {
            case SUCCESS:
                return true;
            case WRONG_USAGE:
                commandSender.sendMessage(" Brug: §b" + result.getSubCommand().getUsage(label));
                return true;
            case NO_PERMISSION:
                commandSender.sendMessage(" Du har ikke adgang til at bruge denne kommando.");
                return true;
            default:
                commandSender.sendMessage("");
                commandSender.sendMessage("§bCommands:");
                for (SubCommand subCommand : getSubCommands()) {
                    if (!hasPermission(commandSender, subCommand.getPermissions())) continue;
                    commandSender.sendMessage(" §f- §b" + subCommand.getUsage(label) + " §f" + command.getDescription());
                }
                commandSender.sendMessage("");
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        return getAllowedSubCommands(commandSender, command, s, strings);
    }
}
