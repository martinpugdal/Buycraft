package dk.martinersej.buycraft.utils;

import org.bukkit.inventory.Inventory;

public class InventoryUtils {

    public static boolean inventoryCompare(Inventory inventory1, Inventory inventory2) {
        if (inventory1 == null || inventory2 == null) {
            return false;
        }
        if (inventory1.getSize() != inventory2.getSize()) {
            return false;
        }
        if (!inventory1.getTitle().equals(inventory2.getTitle())) {
            return false;
        }
        if (inventory1.getContents().length != inventory2.getContents().length) {
            return false;
        }
        for (int i = 0; i < inventory1.getSize(); i++) {
            if (inventory1.getItem(i) == null && inventory2.getItem(i) != null) {
                return false;
            }
            if (inventory1.getItem(i) != null && inventory2.getItem(i) == null) {
                return false;
            }
            if (inventory1.getItem(i) != null && inventory2.getItem(i) != null) {
                if (!ItemUtils.isSimilar(inventory1.getItem(i), inventory2.getItem(i))) {
                    return false;
                }
            }
        }
        return true;
    }
}
