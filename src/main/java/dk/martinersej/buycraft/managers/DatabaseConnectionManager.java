package dk.martinersej.buycraft.managers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class DatabaseConnectionManager {

    private final JavaPlugin plugin;
    private final String connectionString;
    private final Lock lock = new ReentrantLock(true);
    private Connection connection;

    public DatabaseConnectionManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.connectionString = String.format("jdbc:sqlite:%s%s%s.db", plugin.getDataFolder(), File.separator, plugin.getDescription().getName());
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    public void connect(Consumer<Connection> callback) {
        asyncFuture(() -> {
            if (connection == null) {
                try {
                    connection = DriverManager.getConnection(connectionString);
                } catch (SQLException ex) {
                    //noinspection CallToPrintStackTrace
                    ex.printStackTrace();
                }
            }
            callback.accept(connection);
        });
    }

    public void syncConnect(Consumer<Connection> callback) {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(connectionString);
            } catch (SQLException ex) {
                //noinspection CallToPrintStackTrace
                ex.printStackTrace();
            }
        }
        callback.accept(connection);
    }

    public void close() throws SQLException {
        connection.close();
    }

    private CompletableFuture<Void> asyncFuture(Runnable runnable) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            lock.lock();
            try {
                runnable.run();
            } finally {
                lock.unlock();
            }
            future.complete(null);
        });
        return future;
    }
}