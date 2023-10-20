package dk.martinersej.buycraft.listeners;

import dk.martinersej.buycraft.BuyCraft;
import dk.martinersej.buycraft.event.PaymentBroadcastEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class OnPaymentBroadcast implements Listener {

    private final List<String> BROADCAST_MESSAGES = BuyCraft.getConfigManager().getConfig().getStringList("buycraft.payment.broadcast.broadcasts");
    private final List<String> PLAYER_MESSAGES = BuyCraft.getConfigManager().getConfig().getStringList("buycraft.payment.message.messages");

    @EventHandler
    public void onPaymentBroadcast(PaymentBroadcastEvent event) {
        if (event.isCancelled()) {
            return;
        }
        String amount = String.valueOf(event.getAmountBought());
        if (event.getAmountBought() % 1 == 0) {
            amount = String.valueOf((long) event.getAmountBought());
        }
        switch (event.getType()) {
            case "BROADCAST":
                for (String message : BROADCAST_MESSAGES) {
                    message = message.replace("%player%", event.getcPlayer().getPlayer().getName());
                    message = message.replace("%amount%", amount);
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
                break;
            case "MESSAGE":
                for (String message : PLAYER_MESSAGES) {
                    message = message.replace("%player%", event.getcPlayer().getPlayer().getName());
                    message = message.replace("%amount%", amount);
                    event.getcPlayer().getPlayer().getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
                break;
            default:
                event.setCancelled(true);
        }

    }
}
