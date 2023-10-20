package dk.martinersej.buycraft.managers.sqlite;

import com.google.gson.Gson;
import dk.martinersej.buycraft.managers.DatabaseConnectionManager;
import dk.martinersej.buycraft.managers.SQLiteManager;
import dk.martinersej.buycraft.models.BPlayer;
import dk.martinersej.buycraft.utils.ItemUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class BPlayerManager extends SQLiteManager {

    private final HashMap<UUID, BPlayer> bPlayers = new HashMap<>();
    private final WeakHashMap<Player, Boolean> buyingCoins = new WeakHashMap<>();
    private final Gson gson = new Gson();

    public BPlayerManager(DatabaseConnectionManager databaseConnectionManager) {
        super(databaseConnectionManager);
        setTables(new String[]{SQL_QUERIES.CREATE_USERS_IF_NOT_EXISTS.toString(), SQL_QUERIES.CREATE_ITEMS_IF_NOT_EXISTS.toString()});
        createTables(() -> databaseConnectionManager.connect(connection -> {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_QUERIES.LOAD_ALL_DATA.toString());
                ResultSet resultSet = preparedStatement.executeQuery();
                int count = 0;
                while (resultSet.next()) {
                    count++;
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    BPlayer bPlayer = bPlayers.computeIfAbsent(uuid, k -> new BPlayer(uuid));
                    String itemString = resultSet.getString("item");
                    if (itemString != null) {
                        ItemStack itemStack = this.decodeItem(itemString);
                        bPlayer.getItems().add(itemStack);
                    }
                }
                System.out.println("[BUYCRAFT]" + "Loaded " + count + " elements from database");
            } catch (Exception e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }
        }));
    }

    public String encodeItem(ItemStack itemStack) {
        Map<String, Object> map = ItemUtils.getItemAsMap(itemStack);
        return gson.toJson(map);
    }

    public ItemStack decodeItem(String string) {
        HashMap<String, Object> map = gson.fromJson(string, HashMap.class);
        return ItemUtils.getItemBuilt(map).build();
    }

    public WeakHashMap<Player, Boolean> getBuyingCoins() {
        return buyingCoins;
    }

    public void createPlayer(UUID uuid) {
        getDatabase().connect(connection -> {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_QUERIES.CREATE_USER.toString());
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }
        });
        bPlayers.put(uuid, new BPlayer(uuid));
    }

    public BPlayer getPlayer(UUID uuid) {
        if (bPlayers.containsKey(uuid)) {
            return bPlayers.get(uuid);
        }
        return null;
    }

    public BPlayer getPlayer(OfflinePlayer player) {
        if (bPlayers.containsKey(player.getUniqueId())) {
            return bPlayers.get(player.getUniqueId());
        }
        return null;
    }

    public void save(BPlayer bPlayer) {
        getDatabase().connect(connection -> {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_QUERIES.UPDATE_COINS_FOR_USER.toString());
                preparedStatement.setString(1, bPlayer.getUUID().toString());
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }
        });
    }

    public void saveItem(BPlayer bPlayer, ItemStack... values) {
        getDatabase().connect(connection -> {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_QUERIES.CREATE_ITEM.toString());
                for (ItemStack itemStack : values) {
                    preparedStatement.setString(1, bPlayer.getUUID().toString());
                    preparedStatement.setString(2, encodeItem(itemStack));
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
            } catch (Exception e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }
        });
    }

    public void removeItem(BPlayer bPlayer, ItemStack... values) {
        getDatabase().connect(connection -> {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_QUERIES.DELETE_ITEM.toString());
                for (ItemStack itemStack : values) {
                    preparedStatement.setString(1, bPlayer.getUUID().toString());
                    preparedStatement.setString(2, encodeItem(itemStack));
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
            } catch (Exception e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }
        });
    }

    enum SQL_QUERIES {
        CREATE_USERS_IF_NOT_EXISTS("CREATE TABLE IF NOT EXISTS users (\n" + "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" + "    uuid VARCHAR(36) NOT NULL UNIQUE,\n" + "    coins BIGINT NOT NULL DEFAULT 0\n" + ");"),
        CREATE_ITEMS_IF_NOT_EXISTS("CREATE TABLE IF NOT EXISTS items (\n" + "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" + "    uuid VARCHAR(36) NOT NULL,\n" + "    item VARCHAR NOT NULL\n" + ");"),
        LOAD_ALL_DATA("SELECT users.uuid, users.coins, items.item FROM users\n" + "LEFT JOIN items ON users.uuid = items.uuid;"),
        CREATE_USER("INSERT INTO users (uuid) VALUES (?)"),
        CREATE_ITEM("INSERT INTO items (uuid, item) VALUES (?, ?)"),
        UPDATE_COINS_FOR_USER("UPDATE users SET coins = ? WHERE uuid = ?"),
        DELETE_ITEM("DELETE FROM items WHERE uuid = ? AND item = ?");
        private final String query;

        SQL_QUERIES(String query) {
            this.query = query;
        }

        @Override
        public String toString() {
            return query;
        }
    }
}
