package dk.martinersej.buycraft.managers;

public abstract class SQLiteManager implements Manager.SQLiteManager {

    private final DatabaseConnectionManager databaseConnectionManager;
    private String[] tables;

    public SQLiteManager(DatabaseConnectionManager databaseConnectionManager) {
        this.databaseConnectionManager = databaseConnectionManager;
    }

    public DatabaseConnectionManager getDatabase() {
        return databaseConnectionManager;
    }

    public String[] getTables() {
        return tables;
    }

    public void setTables(String[] tables) {
        this.tables = tables;
    }
}
