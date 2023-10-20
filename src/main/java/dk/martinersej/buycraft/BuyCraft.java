package dk.martinersej.buycraft;

import dk.martinersej.buycraft.commands.BuyAdminCommand;
import dk.martinersej.buycraft.commands.BuyClaimCommand;
import dk.martinersej.buycraft.commands.BuyCommand;
import dk.martinersej.buycraft.listeners.*;
import dk.martinersej.buycraft.managers.DatabaseConnectionManager;
import dk.martinersej.buycraft.managers.sqlite.BPlayerManager;
import dk.martinersej.buycraft.managers.yaml.BuycraftManager;
import dk.martinersej.buycraft.managers.yaml.ConfigManager;
import dk.martinersej.buycraft.managers.yaml.MessageManager;
import dk.martinersej.buycraft.tasks.DiscountCheckerTask;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;


public final class BuyCraft extends JavaPlugin {

    private static JavaPlugin instance;
    private static boolean placeholderAPIEnabled;
    private static DatabaseConnectionManager databaseConnectionManager;
    private static BPlayerManager bPlayerManager;
    private static BuycraftManager buycraftManager;
    private static MessageManager messageManager;
    private static ConfigManager configManager;

    public static JavaPlugin instance() {
        if (instance == null) {
            throw new NullPointerException("Instance is null");
        }
        return instance;
    }

    public static BuycraftManager getBuycraftManager() {
        return buycraftManager;
    }

    public static BPlayerManager getbPlayerManager() {
        if (bPlayerManager == null) {
            throw new NullPointerException("BPlayerManager is null");
        }
        return bPlayerManager;
    }

    public static MessageManager getMessageManager() {
        if (messageManager == null) {
            throw new NullPointerException("MessageManager is null");
        }
        return messageManager;
    }

    public static ConfigManager getConfigManager() {
        if (configManager == null) {
            throw new NullPointerException("ConfigManager is null");
        }
        return configManager;
    }

    public static boolean isPlaceholderAPIEnabled() {
        return placeholderAPIEnabled;
    }

    @Override
    public void onEnable() {
        instance = this;

        placeholderAPIEnabled = this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null && this.getServer().getPluginManager().getPlugin("PlaceholderAPI").isEnabled();

        runHandlers();
        registerCommands();
        registerEvents();
        runTasks();
    }

    private void runHandlers() {
        databaseConnectionManager = new DatabaseConnectionManager(this);

        bPlayerManager = new BPlayerManager(databaseConnectionManager);

        configManager = new ConfigManager(this, "config.yml");
        messageManager = new MessageManager(this, "messages.yml");
        buycraftManager = new BuycraftManager(this, "menu.yml");
    }

    private void registerCommands() {
        new BuyCommand(this);
        new BuyAdminCommand(this);
        new BuyClaimCommand(this);
    }

    private void registerEvents() {
        this.getServer().getPluginManager().registerEvents(new OnInventoryClick(), this);
        this.getServer().getPluginManager().registerEvents(new OnPlayerJoin(), this);
        this.getServer().getPluginManager().registerEvents(new OnPlayerQuit(), this);
        this.getServer().getPluginManager().registerEvents(new OnPlayerChat(), this);

        if (this.getServer().getPluginManager().getPlugin("UnikPay") != null && this.getServer().getPluginManager().getPlugin("UnikPay").isEnabled()) {
            this.getServer().getPluginManager().registerEvents(new OnBetaling(), this);
        }

        this.getServer().getPluginManager().registerEvents(new OnPaymentReceived(), this);
        this.getServer().getPluginManager().registerEvents(new OnPaymentBroadcast(), this);
    }

    private void runTasks() {
        new DiscountCheckerTask().runTaskTimerAsynchronously(this, 10L, 10 * 20L);
    }

    @Override
    public void onDisable() {
        for (BukkitTask task : this.getServer().getScheduler().getPendingTasks()) {
            task.cancel();
        }
        try {
            databaseConnectionManager.close();
        } catch (SQLException e) {
            this.getLogger().warning("Could not close database connection");
        }
    }
}
