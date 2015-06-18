package com.minecave.pickaxes;

import com.minecave.pickaxes.commands.GiveSwordCommand;
import com.minecave.pickaxes.commands.MainCommand;
import com.minecave.pickaxes.commands.PickaxeCommand;
import com.minecave.pickaxes.commands.SwordCommand;
import com.minecave.pickaxes.config.ConfigValues;
import com.minecave.pickaxes.drops.BlockValues;
import com.minecave.pickaxes.items.GlowEnchant;
import com.minecave.pickaxes.listener.MenuListener;
import com.minecave.pickaxes.listener.PItemListener;
import com.minecave.pickaxes.listener.PlayerListener;
import com.minecave.pickaxes.skill.Skills;
import com.minecave.pickaxes.sql.PlayerInfo;
import com.minecave.pickaxes.sql.QueryThread;
import com.minecave.pickaxes.sql.SQLManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;

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
        Skills.skills = new HashMap<>();
        saveDefaultConfig();

        attemptSaveResource("drops.yml");
        attemptSaveResource("levels.yml");
        attemptSaveResource("xp.yml");
        attemptSaveResource("sql.yml");
        attemptSaveResource("menus.yml");
        attemptSaveResource("skills.yml");
        attemptSaveResource("enchants.yml");

        this.configValues = new ConfigValues(getConfig());
        configValues.init();
        new PItemListener();
        new PlayerListener();
        new MenuListener();
        FileConfiguration section = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "sql.yml"));
        String host = section.getString("host");
        String user = section.getString("user");
        String db = section.getString("db-name");
        String pass = section.getString("password");
        int port = section.getInt("port");
        this.sqlManager = new SQLManager(host, db, user, pass, port);
        getCommand("pick").setExecutor(new MainCommand());
        getCommand("givePick").setExecutor(new PickaxeCommand());
        getCommand("sword").setExecutor(new SwordCommand());
        getCommand("giveSword").setExecutor(new GiveSwordCommand());
        GlowEnchant.register();
        Bukkit.getOnlinePlayers().forEach(sqlManager::init);
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(PlayerInfo::save);
        QueryThread.t.cancel();
        QueryThread.t = null;
        Skills.skills.clear();
        PlayerInfo.getInfoMap().clear();
        BlockValues.values.clear();
    }

    public void attemptSaveResource(String name) {
        if(!(new File(getDataFolder(), name).exists())) {
            saveResource(name, false);
        }
    }

    public SQLManager getSqlManager() {
        return sqlManager;
    }

    public ConfigValues getConfigValues() {
        return configValues;
    }
}
