package dk.martinersej.buycraft.listeners;

import dk.martinersej.buycraft.BuyCraft;
import dk.martinersej.buycraft.event.PaymentBroadcastEvent;
import dk.martinersej.buycraft.event.PaymentReceivedEvent;
import dk.martinersej.coinsystem.CoinSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OnPaymentReceived implements Listener {

    private final boolean BROADCAST_ON_BUY = BuyCraft.getConfigManager().getConfig().getBoolean("buycraft.payment.broadcast.on-buy");

    private final boolean MESSSAGE_ON_BUY = BuyCraft.getConfigManager().getConfig().getBoolean("buycraft.payment.message.on-buy");

    private static final String COINS_BOUGHT_WEBHOOK_URL = BuyCraft.getConfigManager().getConfig().getString("buycraft.webhooks.coins-bought");

    @EventHandler
    public void onPaymentReceived(PaymentReceivedEvent event) {
        if (event.getbPlayer() == null) {
            return;
        }
        CoinSystem.getCoinsManager().getCPlayer(event.getbPlayer().getPlayer()).addCoins(event.getAmountBought());
        if (COINS_BOUGHT_WEBHOOK_URL != null) {
            event.sendWebhook(COINS_BOUGHT_WEBHOOK_URL);
        }
        if (BROADCAST_ON_BUY) {
            new PaymentBroadcastEvent(event.getbPlayer(), event.getAmountBought(), "BROADCAST").call();
        }
        if (MESSSAGE_ON_BUY) {
            new PaymentBroadcastEvent(event.getbPlayer(), event.getAmountBought(), "MESSAGE").call();
        }
    }
}
