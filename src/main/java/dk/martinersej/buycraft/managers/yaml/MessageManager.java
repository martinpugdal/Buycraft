package dk.martinersej.buycraft.managers.yaml;

import dk.martinersej.buycraft.managers.YamlManager;
import dk.martinersej.coinsystem.CoinSystem;
import dk.martinersej.coinsystem.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MessageManager extends YamlManager {

    public MessageManager(JavaPlugin plugin, String configName) {
        super();
        setupConfig(plugin, configName);
        load();
    }

    public String getMessage(String messagePath) {
        String message = getConfig().getString(messagePath);
        if (getConfig().isList(messagePath)) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String msg : getConfig().getStringList(messagePath)) {
                stringBuilder.append(msg).append("\n");
            }
            message = stringBuilder.toString();
        }
        if (message == null) {
            message = "Message not found: " + messagePath;
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getMessage(Player sender, String messagePath, String[] placeholders, String[] values) {
        String message = replacePlaceholders(getMessage(messagePath), sender);
        for (int i = 0; i < placeholders.length; i++) {
            message = message.replaceAll(placeholders[i], values[i]);
        }
        return message;
    }

    private String replacePlaceholders(String message, OfflinePlayer player) {
        message = message.replaceAll("%player%", player.getName());
        message = message.replaceAll("%player_name%", player.getName());
        message = message.replaceAll("%player_displayname%", player.isOnline() ? player.getPlayer().getDisplayName() : player.getName());
        message = message.replaceAll("%player_uuid%", player.getUniqueId().toString());
        message = message.replaceAll("%player_coins%", StringUtils.formatBigDecimal(CoinSystem.getCoinsManager().getCPlayer(player).getCoins()));
        return message;
    }

    public void sendMessage(Player player, String messagePath) {
        String message = replacePlaceholders(getMessage(messagePath), player);
        player.sendMessage(message);
    }

    public void sendMessage(Player sender, String messagePath, String[] placeholders, String[] values) {
        sender.sendMessage(getMessage(sender, messagePath, placeholders, values));
    }
}