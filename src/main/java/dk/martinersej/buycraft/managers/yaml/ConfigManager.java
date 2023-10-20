package dk.martinersej.buycraft.managers.yaml;

import dk.martinersej.buycraft.BuyCraft;
import dk.martinersej.buycraft.managers.YamlManager;
import dk.martinersej.buycraft.models.Discount;
import dk.martinersej.buycraft.models.Product;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager extends YamlManager {


    List<Discount> discounts = new ArrayList<>();
    List<Discount> activeDiscounts = new ArrayList<>();

    public ConfigManager(JavaPlugin plugin, String configName) {
        super();
        setupConfig(plugin, configName);
        load();
    }

    @Override
    public void load() {
        super.load();
        loadDiscountsByConfig();
    }

    private void loadDiscountsByConfig() {
        activeDiscounts.clear();
        discounts.clear();
        String discountPath = "buycraft.discount.";
        if (getConfig().contains(discountPath + "discounts")) {
            List<Map<?, ?>> itemList = getConfig().getMapList(discountPath + "discounts");
            for (Map<?, ?> itemMap : itemList) {
                HashMap<String, Object> itemHashMap = new HashMap<>();
                for (Map.Entry<?, ?> entry : itemMap.entrySet()) {
                    itemHashMap.put(entry.getKey().toString(), entry.getValue());
                }
                Discount discount = new Discount();
                discount.serialize(getConfig(), itemHashMap);
                if (discount.isStarted() && !discount.isExpired()) {
                    activeDiscounts.add(discount);
                } else {
                    discounts.add(discount);
                }
            }
        }
    }

    public List<Discount> getDiscounts() {
        return discounts;
    }

    public List<Discount> getActiveDiscounts() {
        return activeDiscounts;
    }

    public void removeActiveDiscount(Discount discount) {
        activeDiscounts.remove(discount);
        for (Product product : discount.getProducts()) {
            product.getActiveDiscounts().remove(discount);
//            product.getDiscounts().remove(discount);
        }
        discount.getProducts().clear();
    }

    public void addActiveDiscount(Discount discount) {
        activeDiscounts.add(discount);
        for (Product product : BuyCraft.getBuycraftManager().getProducts()) {
            if (product.getDiscounts().contains(discount)) {
                product.getActiveDiscounts().add(discount);
            }
            product.getDiscounts().remove(discount);
        }
    }
}
