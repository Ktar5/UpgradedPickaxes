package com.minecave.pickaxes;

import com.minecave.pickaxes.commands.GiveCommand;
import com.minecave.pickaxes.commands.PickCommand;
import com.minecave.pickaxes.commands.SwordCommand;
import com.minecave.pickaxes.drops.DropManager;
import com.minecave.pickaxes.enchant.PEnchantManager;
import com.minecave.pickaxes.item.PItemManager;
import com.minecave.pickaxes.level.LevelManager;
import com.minecave.pickaxes.listener.MenuListener;
import com.minecave.pickaxes.listener.PItemListener;
import com.minecave.pickaxes.listener.PlayerListener;
import com.minecave.pickaxes.menu.MenuManager;
import com.minecave.pickaxes.player.PlayerManager;
import com.minecave.pickaxes.skill.PSkillManager;
import com.minecave.pickaxes.util.config.CustomConfig;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class EnhancedPicks extends JavaPlugin {

    @Getter
    private static EnhancedPicks instance;
    @Getter
    private Map<String, CustomConfig> configMap;
    @Getter
    private LevelManager levelManager;
    @Getter
    private PSkillManager pSkillManager;
    @Getter
    private DropManager dropManager;
    @Getter
    private PEnchantManager pEnchantManager;
    @Getter
    private MenuManager menuManager;
    @Getter
    private PItemManager pItemManager;
    @Getter
    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        instance = this;
        configMap = new HashMap<>();
        saveDefaultConfig();

        saveDefaultConfig("config");
        saveDefaultConfig("drops");
        saveDefaultConfig("levels");
        saveDefaultConfig("xp");
        saveDefaultConfig("menus");
        saveDefaultConfig("skills");
        saveDefaultConfig("enchants");
        saveDefaultConfig("picks");
        saveDefaultConfig("swords");

        levelManager = new LevelManager();
        pSkillManager = new PSkillManager();
        dropManager = new DropManager();
        pEnchantManager = new PEnchantManager();
        menuManager = new MenuManager();
        pItemManager = new PItemManager();
        playerManager = new PlayerManager();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new MenuListener(), this);
        pm.registerEvents(new PItemListener(), this);
        pm.registerEvents(new PlayerListener(), this);

        getCommand("pgive").setExecutor(new GiveCommand());
        getCommand("pick").setExecutor(new PickCommand());
        getCommand("sword").setExecutor(new SwordCommand());

        Bukkit.getOnlinePlayers().forEach(playerManager::load);
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(playerManager::save);
    }

    public void saveDefaultConfig(String name) {
        String fileName = name + ".yml";
        configMap.put(name, new CustomConfig(getDataFolder(), fileName));
    }

    public CustomConfig getConfig(String name) {
        return configMap.get(name);
    }
}
