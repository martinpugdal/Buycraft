package dk.martinersej.buycraft.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemBuilder {

    private final ItemStack itemStack;

    public ItemBuilder() {
        this(Material.BARRIER);
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemBuilder(Material material) {
        this(new ItemStack(material));
    }

    private String convertToColor(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public ItemBuilder setType(Material material) {
        itemStack.setType(material);
        return this;
    }

    public ItemBuilder setMaterial(Material material) {
        itemStack.setType(material);
        return this;
    }

    public ItemBuilder setItemMeta(ItemMeta itemMeta) {
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setItemStack(ItemStack itemStack) {
        itemStack.setItemMeta(itemStack.getItemMeta());
        return this;
    }

    public ItemBuilder setDisplayName(String name) {
        name = convertToColor(name);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addLore(String... lore) {
        for (int i = 0; i < lore.length; i++) {
            lore[i] = convertToColor(lore[i]);
        }
        ItemMeta meta = itemStack.getItemMeta();
        java.util.List<String> lores = meta.getLore();
        lores.addAll(java.util.Arrays.asList(lore));
        meta.setLore(lores);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addLore(java.util.List<String> lore) {
        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, convertToColor(lore.get(i)));
        }
        ItemMeta meta = itemStack.getItemMeta();
        java.util.List<String> lores = meta.getLore();
        lores.addAll(lore);
        meta.setLore(lores);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeLore(String... lore) {
        for (int i = 0; i < lore.length; i++) {
            lore[i] = convertToColor(lore[i]);
        }
        ItemMeta meta = itemStack.getItemMeta();
        java.util.List<String> lores = meta.getLore();
        lores.removeAll(java.util.Arrays.asList(lore));
        meta.setLore(lores);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeLore(java.util.List<String> lore) {
        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, convertToColor(lore.get(i)));
        }
        ItemMeta meta = itemStack.getItemMeta();
        java.util.List<String> lores = meta.getLore();
        lores.removeAll(lore);
        meta.setLore(lores);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder clearLore() {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(null);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder setDurability(short durability) {
        itemStack.setDurability(durability);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.spigot().setUnbreakable(unbreakable);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setGlowing(boolean glowing) {
        if (glowing) {
            ItemMeta meta = itemStack.getItemMeta();
            meta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder addEnchantment(org.bukkit.enchantments.Enchantment enchantment, int level) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.addEnchant(enchantment, level, true);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeEnchantment(org.bukkit.enchantments.Enchantment enchantment) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.removeEnchant(enchantment);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder clearEnchantments() {
        ItemMeta meta = itemStack.getItemMeta();
        for (org.bukkit.enchantments.Enchantment enchantment : meta.getEnchants().keySet()) {
            meta.removeEnchant(enchantment);
        }
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addItemFlag(org.bukkit.inventory.ItemFlag itemFlag) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(itemFlag);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeItemFlag(org.bukkit.inventory.ItemFlag itemFlag) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.removeItemFlags(itemFlag);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder clearItemFlags() {
        ItemMeta meta = itemStack.getItemMeta();
        meta.removeItemFlags(org.bukkit.inventory.ItemFlag.values());
        itemStack.setItemMeta(meta);
        return this;
    }

    public SkullBuilder skull() {
        if (!SkullBuilder.isSkull(itemStack)) {
            this.setType(Material.SKULL_ITEM);
            this.setDurability((short) 3);
        }
        return new SkullBuilder(this.build());
    }

    public ItemStack build() {
        return itemStack;
    }

    public List<String> getLore() {
        return itemStack != null && itemStack.getItemMeta() != null ? itemStack.getItemMeta().getLore() : null;
    }

    public ItemBuilder setLore(String... lore) {
        for (int i = 0; i < lore.length; i++) {
            lore[i] = convertToColor(lore[i]);
        }
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(java.util.Arrays.asList(lore));
        itemStack.setItemMeta(meta);
        return this;

    }

    public ItemBuilder setLore(java.util.List<String> lore) {
        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, convertToColor(lore.get(i)));
        }
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return this;

    }
}