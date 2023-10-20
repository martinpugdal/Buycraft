package dk.martinersej.buycraft.models;

import dk.martinersej.buycraft.BuyCraft;
import dk.martinersej.buycraft.utils.ItemBuilder;
import dk.martinersej.buycraft.utils.ItemUtils;
import dk.martinersej.coinsystem.CoinSystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Menu {

    private MenuType menuType;
    private Inventory inventory;
    private Item[] items;


    public Menu() {
        this.menuType = MenuType.SUB;
    }

    public Menu serialize(FileConfiguration config, String menuName) {
        String menuPath = "menus." + menuName + ".";
        this.menuType = config.getBoolean(menuPath + "home") ? MenuType.MAIN : MenuType.SUB;
        InventoryType inventoryType = InventoryType.CHEST;
        try {
            inventoryType = InventoryType.valueOf(config.getString("menus." + menuName + ".type").toUpperCase());
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("InventoryType not found: " + config.getString("menus." + menuName + ".type"));
        }
        String title = ChatColor.translateAlternateColorCodes('&', config.getString("menus." + menuName + ".title"));
        int rows = config.getInt("menus." + menuName + ".rows");
        this.items = new Item[rows * 9];
        if (inventoryType.equals(InventoryType.CHEST)) {
            this.inventory = Bukkit.createInventory(null, rows * 9, title);
        } else {
            this.inventory = Bukkit.createInventory(null, inventoryType, title);
        }

        for (String slot : config.getConfigurationSection(menuPath + "slots").getKeys(false)) {
            boolean between2Slots = slot.indexOf('-') != -1 && slot.indexOf("-") == slot.lastIndexOf('-') && Integer.parseInt(slot.split("-")[0]) < Integer.parseInt(slot.split("-")[1]);
            int slot1 = between2Slots ? Integer.parseInt(slot.split("-")[0]) : Integer.parseInt(slot);
            int slot2 = between2Slots ? Integer.parseInt(slot.split("-")[1]) : -1;
            String slotPath = menuPath + "slots." + slot + ".";
            String itemPath = slotPath + "item.";
            ItemBuilder itemBuilder = ItemUtils.getItemBuilt(config, itemPath);
            for (int i = slot1; i < (slot2 == -1 ? slot1 + 1 : slot2 + 1); i++) {
                this.setItem(i, itemBuilder.build());
            }
            if (config.getString(slotPath + "product") != null) {
                String productPath = slotPath + "product.";
                this.getItem(slot1).setProduct(new Product().serialize(config, productPath));
                updateItemAtSlot(slot1);
            }
            if (config.getString(slotPath + "close-on-click") != null) {
                this.getItem(slot1).setCloseMenu(config.getBoolean(slotPath + "close-on-click"));
            }
            if (config.getString(slotPath + "openmenu") != null) {
                String openMenu = config.getString(slotPath + "openmenu");
                if (openMenu != null) {
                    this.getItem(slot1).setOpenMenu(openMenu);
                }
            }

            if (config.getString(slotPath + "buy-coins") != null) {
                this.getItem(slot1).setBuyCoins(true);
            }

        }
        return this;
    }

    public Item getItem(int slotInt) {
        return items[slotInt] != null ? items[slotInt] : new Item(new ItemStack(Material.AIR));
    }

    public MenuType getMenuType() {
        return menuType;
    }

    public void setItem(int slot, ItemStack item) {
        this.inventory.setItem(slot, item);
        this.items[slot] = new Item(item);
    }

    public void updateItemAtSlot(int slot) {
        ItemMeta itemMeta = this.getItem(slot).getItemStack().clone().getItemMeta();
        List<String> lores = itemMeta.getLore() != null ? itemMeta.getLore() : new ArrayList<>();
        if (this.getItem(slot).getProduct() != null) {
            lores.replaceAll(s -> s.replaceAll("%price%", String.valueOf((long) this.getItem(slot).getProduct().getPrice())));
        }
        itemMeta.setLore(lores);
        ItemStack itemStack = this.getItem(slot).getItemStack().clone();
        itemStack.setItemMeta(itemMeta);
        this.inventory.setItem(slot, itemStack);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void onClick(Player player, int slot) {
        Item item = this.getItem(slot);
        if (item == null) {
            return;
        }
        if (item.isCloseMenu()) {
            player.closeInventory();
        }
        if (item.getOpenMenu() != null) {
            Menu menu = BuyCraft.getBuycraftManager().getMenuByMenuName(item.getOpenMenu());
            Inventory inventory = Bukkit.createInventory(null, menu.getInventory().getSize(), menu.getInventory().getTitle());
            inventory.setContents(menu.getInventory().getContents());
            player.openInventory(BuyCraft.getBuycraftManager().updateStaticItems(player, inventory));
            return;
        }
        if (item.isBuyCoins()) {
            player.closeInventory();
            runCoinsBuy(player);
            return;
        }

        if (item.getProduct() == null) {
            return;
        }
        double coins = CoinSystem.getCoinsManager().getCPlayer(player).getCoins().floatValue();
        double price = item.getProduct().getPrice();
        if (price > coins) {
            BuyCraft.getMessageManager().sendMessage(player, "buycraft.coins.not-enough-coins", new String[]{"%coins_amount%"}, new String[]{String.valueOf(item.getProduct().getPrice() - coins)});
            return;
        }

        if (item.getProduct().isOneTimePurchase()) {
            if (player.hasPermission(item.getProduct().getPermissionCheckFor())) {
                BuyCraft.getMessageManager().sendMessage(player, "buycraft.already-bought", new String[]{"%product_name%"}, new String[]{item.getItemStack().getItemMeta().getDisplayName() != null ? item.getItemStack().getItemMeta().getDisplayName() : ""});
                return;
            }
        }
        CoinSystem.getCoinsManager().getCPlayer(player).removeCoins(price);

        for (String command : item.getProduct().getCommands()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
        }
        for (String broadcast : item.getProduct().getBroadcasts()) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', broadcast.replace("%player%", player.getName())));
        }
        for (String message : item.getProduct().getMessages()) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message.replace("%player%", player.getName())));
        }

        ItemStack[] itemStacks = item.getProduct().getItems().toArray(new ItemStack[0]);
        HashMap<Integer, ItemStack> itemsNotFitIn = player.getInventory().addItem(itemStacks);
        if (!itemsNotFitIn.isEmpty()) {
            BuyCraft.getMessageManager().sendMessage(player, "buycraft.inventory-full");
        }
        BuyCraft.getbPlayerManager().getPlayer(player.getUniqueId()).addItems(itemsNotFitIn.values());
    }

    private void runCoinsBuy(Player player) {
        BuyCraft.getbPlayerManager().getBuyingCoins().put(player, true);
        BuyCraft.getMessageManager().sendMessage(player, "buycraft.coins.send-start-message", new String[]{"%cancel_word%"}, new String[]{BuyCraft.getMessageManager().getMessage("buycraft.coins.cancel-word")});
    }

    public void update() {
        for (int i = 0; i < this.items.length; i++) {
            if (this.items[i] != null) {
                updateItemAtSlot(i);
            }
        }
    }

    public enum MenuType {
        MAIN, SUB
    }
}
