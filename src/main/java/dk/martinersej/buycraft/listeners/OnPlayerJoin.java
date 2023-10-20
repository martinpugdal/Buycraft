package dk.martinersej.buycraft.listeners;

import dk.martinersej.buycraft.BuyCraft;
import dk.martinersej.buycraft.event.PaymentBroadcastEvent;
import dk.martinersej.buycraft.models.BPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnPlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        BPlayer bPlayer = BuyCraft.getbPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        if (bPlayer == null) {
            BuyCraft.getbPlayerManager().createPlayer(event.getPlayer().getUniqueId());
            return;
        }
    }
}
