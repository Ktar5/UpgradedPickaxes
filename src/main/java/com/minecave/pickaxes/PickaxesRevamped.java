package com.minecave.pickaxes;

import com.minecave.pickaxes.commands.MainCommand;
import com.minecave.pickaxes.commands.PickaxeCommand;
import com.minecave.pickaxes.config.ConfigValues;
import com.minecave.pickaxes.listener.MenuListener;
import com.minecave.pickaxes.listener.PItemListener;
import com.minecave.pickaxes.listener.PlayerListener;
import com.minecave.pickaxes.sql.SQLManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Created by Carter on 6/4/2015.
 */
public class PickaxesRevamped extends JavaPlugin {

    private static PickaxesRevamped instance;
    private SQLManager sqlManager;
    private ConfigValues configValues;

    public static PickaxesRevamped getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        this.configValues = new ConfigValues(getConfig());
        new PItemListener();
        new PlayerListener();
        new MenuListener();
        saveResource("drops.yml", false);
        saveResource("levels.yml", false);
        saveResource("xp.yml", false);
        saveResource("sql.yml", false);
        saveResource("menus.yml", false);
        saveResource("skills.yml", false);
        FileConfiguration section = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "sql.yml"));
        String host = section.getString("host");
        String user = section.getString("user");
        String db = section.getString("db-name");
        String pass = section.getString("pass");
        int port = section.getInt("port");
        this.sqlManager = new SQLManager(host, db, user, pass, port);
        getCommand("pick").setExecutor(new MainCommand());
        getCommand("givePick").setExecutor(new PickaxeCommand());
    }

    @Override
    public void onDisable(){
        instance = null;
    }

    public SQLManager getSqlManager() {
        return sqlManager;
    }

    public ConfigValues getConfigValues() {
        return configValues;
    }
}
