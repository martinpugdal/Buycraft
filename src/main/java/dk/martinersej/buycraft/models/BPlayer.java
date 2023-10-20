package dk.martinersej.buycraft.models;

import dk.martinersej.buycraft.BuyCraft;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class BPlayer {


    private final UUID uuid;
    private final ArrayList<ItemStack> items = new ArrayList<>();

    public BPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUUID() {
        return uuid;
    }

    public OfflinePlayer getPlayer() {
        return BuyCraft.instance().getServer().getOfflinePlayer(uuid);
    }

    public ArrayList<ItemStack> getItems() {
        return items;
    }

    public void addItems(Collection<ItemStack> values) {
        this.items.addAll(values);
        BuyCraft.getbPlayerManager().saveItem(this, values.toArray(new ItemStack[0]));
    }

    public void removeItems(Collection<ItemStack> values) {
        this.items.removeAll(values);
        BuyCraft.getbPlayerManager().removeItem(this, values.toArray(new ItemStack[0]));
    }
}
