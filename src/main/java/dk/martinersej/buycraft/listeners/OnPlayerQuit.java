package dk.martinersej.buycraft.listeners;

import dk.martinersej.buycraft.BuyCraft;
import dk.martinersej.buycraft.models.BPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnPlayerQuit implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        BPlayer bPlayer = BuyCraft.getbPlayerManager().getPlayer(player.getUniqueId());
        if (bPlayer == null) {
            Bukkit.getLogger().warning("Player " + player.getName() + " was not found in the database!");
        } else {
            BuyCraft.getbPlayerManager().save(bPlayer);
        }
    }
}
