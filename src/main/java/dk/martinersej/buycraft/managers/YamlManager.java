package dk.martinersej.buycraft.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class YamlManager implements Manager.YamlManager {

    private String filePath = "";
    private FileConfiguration config = new YamlConfiguration();

    public YamlManager() {
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void setConfig(FileConfiguration fileConfiguration) {
        this.config = fileConfiguration;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath1) {
        filePath = filePath1;
    }
}