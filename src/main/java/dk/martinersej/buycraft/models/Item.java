package dk.martinersej.buycraft.models;

import org.bukkit.inventory.ItemStack;

public class Item {

    private final ItemStack itemStack;
    private boolean closeMenu = false;
    private Product product = null;
    private String openMenu = null;
    private boolean buyCoins = false;

    public Item(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getOpenMenu() {
        return openMenu;
    }

    public void setOpenMenu(String openMenu) {
        this.openMenu = openMenu;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public boolean isCloseMenu() {
        return closeMenu;
    }

    public void setCloseMenu(boolean closeMenu) {
        this.closeMenu = closeMenu;
    }

    public boolean isBuyCoins() {
        return buyCoins;
    }

    public void setBuyCoins(boolean b) {
        this.buyCoins = b;
    }
}
