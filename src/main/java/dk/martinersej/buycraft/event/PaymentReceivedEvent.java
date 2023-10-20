package dk.martinersej.buycraft.event;

import dk.martinersej.buycraft.BuyCraft;
import dk.martinersej.buycraft.models.BPlayer;
import dk.martinersej.buycraft.utils.DiscordWebhook;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.awt.*;
import java.io.IOException;

public class PaymentReceivedEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private final float amountBought;
    private final BPlayer bPlayer;
    private final OfflinePlayer offlinePlayer;

    public PaymentReceivedEvent(OfflinePlayer player, float amountBought) {
        this.offlinePlayer = player;
        this.bPlayer = BuyCraft.getbPlayerManager().getPlayer(player);
        this.amountBought = amountBought;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public void call() {
        Bukkit.getPluginManager().callEvent(this);
    }

    public float getAmountBought() {
        return amountBought;
    }

    public BPlayer getbPlayer() {
        return bPlayer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public OfflinePlayer getOfflinePlayer() {
        return offlinePlayer;
    }

    public void sendWebhook(String url) {
        DiscordWebhook discordWebhook = new DiscordWebhook(url);
        discordWebhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setTitle("Coins Bought")
                .setDescription("A player has bought coins!")
                .setColor(Color.GREEN)
                .addField("Player", this.getbPlayer().getPlayer().getName(), true)
                .addField("Amount", String.valueOf(this.getAmountBought()), true)
                .setFooter(this.getbPlayer().getPlayer().getUniqueId().toString(), null)
        );
        try {
            discordWebhook.execute();
        } catch (IOException e) {
            BuyCraft.instance().getLogger().warning("Failed to send webhook!");
        }
    }
}