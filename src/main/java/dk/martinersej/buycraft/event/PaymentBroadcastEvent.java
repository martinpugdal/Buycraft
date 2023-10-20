package dk.martinersej.buycraft.event;

import dk.martinersej.buycraft.models.BPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PaymentBroadcastEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();
    private final BPlayer bPlayer;
    private final String type;
    private final float amountBought;
    private boolean cancelled;

    public PaymentBroadcastEvent(BPlayer player, float amountBought, String type) {
        this.bPlayer = player;
        this.amountBought = amountBought;
        this.type = type.toUpperCase();
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public void call() {
        Bukkit.getPluginManager().callEvent(this);
    }

    public BPlayer getcPlayer() {
        return bPlayer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public String getType() {
        return type;
    }

    public float getAmountBought() {
        return amountBought;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}