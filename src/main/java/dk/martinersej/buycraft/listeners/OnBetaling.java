package dk.martinersej.buycraft.listeners;

import dk.manaxi.unikpay.plugin.API.Internal;
import dk.martinersej.buycraft.event.PaymentReceivedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OnBetaling implements Listener {

    @EventHandler
    public void onBetaling(dk.manaxi.unikpay.plugin.event.OnBetaling event) {
        Internal.acceptPackageRequest(event.getId());
        new PaymentReceivedEvent(event.getPlayer(), event.getAmount()).call();
    }
}