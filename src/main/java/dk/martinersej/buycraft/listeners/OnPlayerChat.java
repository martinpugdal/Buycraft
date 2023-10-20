package dk.martinersej.buycraft.listeners;

import dk.manaxi.unikpay.plugin.API.Internal;
import dk.martinersej.buycraft.BuyCraft;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class OnPlayerChat implements Listener {


    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        if (!BuyCraft.getbPlayerManager().getBuyingCoins().containsKey(player)) {
            return;
        }
        event.setCancelled(true);
        String message = event.getMessage();

        String cancelMessage = BuyCraft.getMessageManager().getMessage("buycraft.coins.cancel-word");
        double messageAsNumber;
        if (message.equalsIgnoreCase(cancelMessage)) {
            BuyCraft.getbPlayerManager().getBuyingCoins().remove(player);
            BuyCraft.getMessageManager().sendMessage(player, "buycraft.coins.cancelled");
            return;
        } else {
            try {
                messageAsNumber = Double.parseDouble(message);
            } catch (NumberFormatException e) {
                BuyCraft.getMessageManager().sendMessage(player, "messages.not-a-number");
                return;
            }
        }
        if (messageAsNumber > 0) {
            if (messageAsNumber < BuyCraft.getConfigManager().getConfig().getInt("buycraft.coins.buy-minimum")) {
                BuyCraft.getMessageManager().sendMessage(player, "buycraft.coins.below-minimum", new String[]{"%minimum_coins%"}, new String[]{String.valueOf(BuyCraft.getConfigManager().getConfig().getInt("buycraft.coins.buy-minimum"))});
                return;
            }
            BuyCraft.getbPlayerManager().getBuyingCoins().remove(player);
            if (BuyCraft.getConfigManager().getConfig().getBoolean("buycraft.payment.unikpay") && BuyCraft.instance().getServer().getPluginManager().getPlugin("UnikPay") != null && BuyCraft.instance().getServer().getPluginManager().getPlugin("UnikPay").isEnabled())
            {
                try {
                    Internal.sendPackageRequest(player, "coins", messageAsNumber, null);
                } catch (Exception ignored) {
                }
            }
        } else {
            BuyCraft.getMessageManager().sendMessage(player, "messages.not-a-number");
        }
    }
}
