package dk.martinersej.buycraft.models;

import dk.martinersej.buycraft.BuyCraft;
import dk.martinersej.buycraft.utils.ItemBuilder;
import dk.martinersej.buycraft.utils.ItemUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Product {

    private final List<String> commands = new ArrayList<>();
    private final List<String> messages = new ArrayList<>();
    private final List<String> broadcasts = new ArrayList<>();
    private final List<ItemStack> items = new ArrayList<>();
    private final List<Discount> discounts = new ArrayList<>();
    private final List<Discount> activeDiscounts = new ArrayList<>();

    private long price = 0;
    private boolean oneTimePurchase;
    private String permissionCheckFor;

    public Product() {
    }

    public double getPrice() {
        long priceAfterDiscount = this.price;
        if (BuyCraft.getConfigManager().getActiveDiscounts().isEmpty()) {
            return priceAfterDiscount;
        }
        for (Discount discount : BuyCraft.getConfigManager().getActiveDiscounts()) {
            if (activeDiscounts.contains(discount) || discount.isAllProducts()) {
                if (priceAfterDiscount > (long) (price * (100 - discount.getDiscount()) / 100)) {
                    priceAfterDiscount = (long) (price * (100 - discount.getDiscount()) / 100);
                }
            }
        }
        return priceAfterDiscount;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public boolean isOneTimePurchase() {
        return oneTimePurchase;
    }

    public String getPermissionCheckFor() {
        return permissionCheckFor;
    }

    public List<Discount> getDiscounts() {
        return this.discounts;
    }

    public List<Discount> getActiveDiscounts() {
        return this.activeDiscounts;
    }

    public List<String> getCommands() {
        return commands;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public String toString() {
        return "Product: " + price + " " + commands + " " + items;
    }

    public List<String> getMessages() {
        return messages;
    }

    public List<String> getBroadcasts() {
        return broadcasts;
    }

    public Product serialize(FileConfiguration config, String productPath) {
        this.setPrice(config.getLong(productPath + "price"));
        this.getCommands().addAll(config.getStringList(productPath + "commands"));
        this.getMessages().addAll(config.getStringList(productPath + "messages"));
        this.getBroadcasts().addAll(config.getStringList(productPath + "broadcasts"));
        if (config.contains(productPath + "items")) {
            List<Map<?, ?>> itemList = config.getMapList(productPath + "items");
            for (Map<?, ?> itemMap : itemList) {
                HashMap<String, Object> itemHashMap = new HashMap<>();
                for (Map.Entry<?, ?> entry : itemMap.entrySet()) {
                    itemHashMap.put(entry.getKey().toString(), entry.getValue());
                }
                ItemBuilder itemBuilt = ItemUtils.getItemBuilt(itemHashMap);
                this.getItems().add(itemBuilt.build());
            }
        }
        if (config.contains(productPath + "discounts")) {
            List<String> discountList = config.getStringList(productPath + "discounts");
            for (String discountName : discountList) {
                for (Discount discount : BuyCraft.getConfigManager().getDiscounts()) {
                    if (discount.getName().equalsIgnoreCase(discountName)) {
                        discounts.add(discount);
                    }
                }
                for (Discount discount : BuyCraft.getConfigManager().getActiveDiscounts()) {
                    if (discount.getName().equalsIgnoreCase(discountName)) {
                        activeDiscounts.add(discount);
                    }
                }
            }
        }
        if (config.contains(productPath + "onetime-purchase")) {
            oneTimePurchase = config.getString(productPath + "onetime-purchase.permission.check-for") != null;
            if (oneTimePurchase) {
                permissionCheckFor = config.getString(productPath + "onetime-purchase.permission.check-for");
            }
        }
        return this;
    }
}
