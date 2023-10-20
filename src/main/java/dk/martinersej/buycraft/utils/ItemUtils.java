package dk.martinersej.buycraft.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemUtils {

    public static ItemBuilder getItemBuilt(HashMap<String, Object> item) {

        ItemBuilder itemBuilder = new ItemBuilder();
        String type = item.get("type").toString();
        short data = Short.parseShort(item.get("data") != null ? String.valueOf((int) Float.parseFloat(String.valueOf(item.get("data")))) : "0");
        boolean glowing = item.get("glowing") != null && Boolean.parseBoolean(item.get("glowing").toString());
        int amount = Integer.parseInt(item.get("amount") != null ? String.valueOf((int) Float.parseFloat(item.get("amount").toString())) : "1");
        String displayName = item.get("display-name") != null ? item.get("display-name").toString() : "";
        List<String> lore = (List<String>) item.get("lore");
        List<String> enchantments = (List<String>) item.get("enchantments");
        try {
            itemBuilder.setMaterial(Material.valueOf(type.toUpperCase().replaceAll(" ", "_")));
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("Material not found: " + type);
            itemBuilder.setMaterial(Material.STONE);
        }
        itemBuilder.setDurability(data);
        if (itemBuilder.build().getType().equals(Material.SKULL_ITEM)) {
            if (item.get("skull-texture") != null) {
                itemBuilder.skull().setSkullOwnerByBase64(item.get("skull-texture").toString());
            } else if (item.get("skull-owner") != null) {
                itemBuilder.skull().setSkullOwner(item.get("skull-owner").toString());
            } else {
                itemBuilder.skull().setSkullOwner("MHF_QUESTION");
            }
        }
        itemBuilder.setGlowing(glowing);
        if (amount > 0) {
            itemBuilder.setAmount(amount);
        } else {
            itemBuilder.setAmount(1);
        }
        if (displayName != null) {
            itemBuilder.setDisplayName(displayName);
        }
        if (lore != null) {
            itemBuilder.setLore(lore);
        }
        if (enchantments != null) {
            for (String enchantment : enchantments) {
                String[] enchantmentSplit = enchantment.split(":");
                if (Enchantment.getByName(enchantmentSplit[0]) != null) {
                    int level = 1;
                    try {
                        level = Integer.parseInt(enchantmentSplit[1]);
                    } catch (NumberFormatException e) {
                        Bukkit.getLogger().warning("Enchantment level is not a number: " + enchantment);
                    }
                    itemBuilder.addEnchantment(Enchantment.getByName(enchantmentSplit[0]), level);
                }
            }
        }
        return itemBuilder;
    }

    public static ItemBuilder getItemBuilt(FileConfiguration config, String itemPath) {
        HashMap<String, Object> item = new HashMap<>();
        item.put("type", config.getString(itemPath + "type"));
        item.put("data", config.getInt(itemPath + "data"));
        item.put("skull-owner", config.getString(itemPath + "skull-owner"));
        item.put("skull-texture", config.getString(itemPath + "skull-texture"));
        item.put("glowing", config.getBoolean(itemPath + "glowing"));
        item.put("amount", config.getInt(itemPath + "amount"));
        item.put("display-name", config.getString(itemPath + "display-name"));
        item.put("lore", config.getStringList(itemPath + "lore"));
        item.put("enchantments", config.getStringList(itemPath + "enchantments"));
        return getItemBuilt(item);
    }

    public static Map<String, Object> getItemAsMap(ItemStack itemStack) {
        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put("type", itemStack.getType().name());
        itemMap.put("data", itemStack.getDurability());
        itemMap.put("glowing", itemStack.containsEnchantment(Enchantment.ARROW_INFINITE));
        itemMap.put("amount", itemStack.getAmount());
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            if (itemMeta.hasDisplayName()) {
                itemMap.put("display-name", itemMeta.getDisplayName());
            }
            if (itemMeta.hasLore()) {
                itemMap.put("lore", new ArrayList<>(itemMeta.getLore()));
            }
            if (!itemMeta.getEnchants().isEmpty()) {
                List<String> enchantments = new ArrayList<>();
                for (Map.Entry<Enchantment, Integer> entry : itemMeta.getEnchants().entrySet()) {
                    String enchantmentString = entry.getKey().getName() + ":" + entry.getValue();
                    enchantments.add(enchantmentString);
                }
                itemMap.put("enchantments", enchantments);
            }
        }
        if (SkullBuilder.isSkull(itemStack)) {
            if (itemStack.getItemMeta() != null && ((SkullMeta) itemStack.getItemMeta()).hasOwner()) {
                itemMap.put("skull-owner", ((SkullMeta) itemStack.getItemMeta()).getOwner());
                itemMap.put("skull-texture", SkullBuilder.getSkullTexture(itemStack));
            }
        }
        return itemMap;
    }

    public static boolean isSimilar(ItemStack stack1, ItemStack stack2) {
        if (stack1 == null) {
            return false;
        } else if (stack2 == null) {
            return true;
        } else if (stack1 == stack2) {
            return true;
        } else {
            return stack1.getTypeId() == stack2.getTypeId() && stack1.getDurability() == stack2.getDurability() && stack1.hasItemMeta() == stack2.hasItemMeta() && (!stack1.hasItemMeta() || stack1.getItemMeta().getDisplayName().equals(stack2.getItemMeta().getDisplayName()));
        }
    }
}
