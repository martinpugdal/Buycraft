package dk.martinersej.buycraft.tasks;

import dk.martinersej.buycraft.BuyCraft;
import dk.martinersej.buycraft.models.Discount;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class DiscountCheckerTask extends BukkitRunnable {

    public DiscountCheckerTask() {
    }

    @Override
    public void run() {
        if (BuyCraft.getConfigManager().getDiscounts().isEmpty() && BuyCraft.getConfigManager().getActiveDiscounts().isEmpty()) {
            cancel();
        }
        for (Discount discount : new ArrayList<>(BuyCraft.getConfigManager().getActiveDiscounts())) {
            if (discount.isExpired()) {
                BuyCraft.getConfigManager().removeActiveDiscount(discount);
                BuyCraft.getBuycraftManager().updateMenus();
            }
        }
        for (Discount discount : new ArrayList<>(BuyCraft.getConfigManager().getDiscounts())) {
            if (discount.isStarted() && !discount.isExpired()) {
                BuyCraft.getConfigManager().addActiveDiscount(discount);
                BuyCraft.getConfigManager().getDiscounts().remove(discount);
                BuyCraft.getBuycraftManager().updateMenus();
                discount.announce();
            }
        }
    }
}

