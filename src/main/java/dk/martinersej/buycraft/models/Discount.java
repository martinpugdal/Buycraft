package dk.martinersej.buycraft.models;

import dk.martinersej.buycraft.BuyCraft;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Discount {
    private final ArrayList<Product> products = new ArrayList<>();
    private List<String> broadcasts = null;
    private String name = null;
    private double discount = 0;
    private LocalDateTime startDate = LocalDateTime.now();
    private LocalDateTime endDate = LocalDateTime.now();
    private boolean allProducts = false;

    public Discount() {
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public boolean isAllProducts() {
        return allProducts;
    }

    public double getDiscount() {
        return discount;
    }

    public void serialize(FileConfiguration config, HashMap<String, Object> itemHashMap) {
        name = (String) itemHashMap.get("name");
        discount = Double.parseDouble(String.valueOf(itemHashMap.get("discount")));
        DateTimeFormatter formatter = config.getString("buycraft.discount.date-format") != null ? DateTimeFormatter.ofPattern(config.getString("buycraft.discount.date-format")) : DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss");
        try {
            startDate = LocalDateTime.parse(String.valueOf(itemHashMap.get("start-date")), formatter);
            endDate = LocalDateTime.parse(String.valueOf(itemHashMap.get("end-date")), formatter);
        } catch (NullPointerException e) {
            Bukkit.getLogger().warning("Discount " + name + " has no start or end date.");
        } catch (DateTimeParseException e) {
            Bukkit.getLogger().warning("Discount " + name + " has an invalid start or end date");
        }
        allProducts = (boolean) itemHashMap.get("discount-on-everything");
        broadcasts = (List<String>) itemHashMap.get("broadcasts");
        broadcasts.replaceAll(s -> s.replace("&", "§"));
        broadcasts.replaceAll(s -> s.replace("%start-date%", startDate.format(formatter)));
        broadcasts.replaceAll(s -> s.replace("%end-date%", endDate.format(formatter)));
        final String discountStr = String.format("%.2f", discount).replaceAll("\\.00$", "");
        broadcasts.replaceAll(s -> s.replace("%discount%", discountStr));
        broadcasts.replaceAll(s -> s.replace("%name%", name));
    }

    public boolean isStarted() {
        return startDate.isBefore(LocalDateTime.now());
    }

    public boolean isExpired() {
        return !endDate.isAfter(LocalDateTime.now());
    }

    @Override
    public String toString() {
        return name;
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public void announce() {
        if (broadcasts == null) {
            return;
        }
        BuyCraft.instance().getServer().broadcastMessage(String.join("\n", broadcasts));
    }
}
