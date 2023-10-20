package dk.martinersej.buycraft.listeners;

import dk.martinersej.buycraft.BuyCraft;
import dk.martinersej.buycraft.models.Menu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;

public class OnInventoryClick implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', BuyCraft.getBuycraftManager().getConfig().getString("claimmenu.title")))) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null) {
                HashMap<Integer, ItemStack> itemsNotFitIn = event.getWhoClicked().getInventory().addItem(event.getCurrentItem());
                if (!itemsNotFitIn.isEmpty()) {
                    event.getWhoClicked().sendMessage(BuyCraft.getMessageManager().getMessage("buycraft.inventory-full-claim"));
                } else {
                    event.getCurrentItem().setType(null);
                    BuyCraft.getbPlayerManager().getPlayer((Player) event.getWhoClicked()).removeItems(Collections.singleton(event.getCurrentItem()));
                }
            }
            return;
        }
        Menu menu = BuyCraft.getBuycraftManager().getMenuByInventory(event.getClickedInventory());
        if (menu != null) {
            event.setCancelled(true);
            menu.onClick((Player) event.getWhoClicked(), event.getSlot());
        }
    }
}
