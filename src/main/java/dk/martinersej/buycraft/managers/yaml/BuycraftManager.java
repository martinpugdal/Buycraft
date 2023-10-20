package dk.martinersej.buycraft.managers.yaml;

import dk.martinersej.buycraft.BuyCraft;
import dk.martinersej.buycraft.managers.YamlManager;
import dk.martinersej.buycraft.models.BPlayer;
import dk.martinersej.buycraft.models.Menu;
import dk.martinersej.buycraft.models.Product;
import dk.martinersej.buycraft.utils.InventoryUtils;
import dk.martinersej.buycraft.utils.ItemBuilder;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BuycraftManager extends YamlManager {

    private static final HashMap<String, Menu> menus = new HashMap<>();

    public BuycraftManager(JavaPlugin plugin, String configName) {
        super();
        setupConfig(plugin, configName);
        load();
    }

    @Override
    public void load() {
        super.load();
        loadMenusByConfig();
    }

    private void loadMenusByConfig() {
        menus.clear();
        for (String menuString : getConfig().getConfigurationSection("menus").getKeys(false)) {
            Menu menu = new Menu().serialize(this.getConfig(), menuString);
            menus.put(menuString, menu);
        }
    }

    public void openBuyMenu(Player commandSender) {
        for (Menu menu : menus.values()) {
            if (menu.getMenuType().equals(Menu.MenuType.MAIN)) {
                Inventory inventoryClone = Bukkit.createInventory(null, menu.getInventory().getSize(), menu.getInventory().getTitle());
                inventoryClone.setContents(menu.getInventory().getContents());
                updateStaticItems(commandSender, inventoryClone);
                commandSender.openInventory(inventoryClone);
                return;
            }
        }
    }

    public Inventory updateStaticItems(Player player, Inventory inventory) {
        if (!BuyCraft.isPlaceholderAPIEnabled()) {
            return inventory;
        }
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemBuilder itemBuilder = new ItemBuilder(inventory.getItem(i));
            if (itemBuilder.getLore() != null) {
                if (PlaceholderAPI.containsPlaceholders(itemBuilder.getLore().toString())) {
                    List<String> lores = PlaceholderAPI.setPlaceholders(player, itemBuilder.getLore());
                    itemBuilder.setLore(lores);
                    inventory.setItem(i, itemBuilder.build());
                }
            }
        }
        return inventory;
    }

    public void openBoughtItemsMenu(Player player) {
        BPlayer bPlayer = BuyCraft.getbPlayerManager().getPlayer(player);
        if (bPlayer == null) {
            return;
        }
        if (bPlayer.getItems().isEmpty()) {
            player.sendMessage(BuyCraft.getMessageManager().getMessage("buycraft.no-items-to-claim"));
            return;
        }
        Inventory gui = Bukkit.createInventory(player, 6 * 9, ChatColor.translateAlternateColorCodes('&', getConfig().getString("claimmenu.title")));
        gui.setContents(bPlayer.getItems().toArray(new ItemStack[0]));
        player.openInventory(gui);
    }

    public Menu getMenuByInventory(Inventory inventory) {
        for (Menu menu : menus.values()) {
            if (InventoryUtils.inventoryCompare(menu.getInventory(), inventory)) {
                return menu;
            }
        }
        return null;
    }

    public Menu getMenuByMenuName(String openMenu) {
        return menus.get(openMenu);
    }

    public List<Product> getProducts() {
        List<Product> products = new ArrayList<>();
        for (Menu menu : menus.values()) {
            int slot = 0;
            int invSize = menu.getInventory().getSize();
            while (slot < invSize) {
                if (menu.getItem(slot) != null) {
                    Product product = menu.getItem(slot).getProduct();
                    if (product != null) {
                        products.add(product);
                    }
                }
                slot++;
            }
        }
        return products;
    }

    public void updateMenus() {
        for (Menu menu : menus.values()) {
            menu.update();
        }
    }
}