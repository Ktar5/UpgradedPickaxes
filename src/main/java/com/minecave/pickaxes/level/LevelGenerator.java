package com.minecave.pickaxes.level;

import com.minecave.pickaxes.PickaxesRevamped;
import com.minecave.pickaxes.builder.FireworkBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

/**
 * @author Timothy Andis
 */
public class LevelGenerator {

    private FileConfiguration config;
    private boolean firework;

    public LevelGenerator(FileConfiguration config, boolean firework) {
        this.config = config;
        this.firework = firework;
        load();
    }

    public void load() {
        ConfigurationSection levels = config.getConfigurationSection("levels");
        if(levels == null) {
            PickaxesRevamped.getInstance().getLogger().warning("No level data found...");
            return;
        }
        for(String s : levels.getKeys(false)) {
            int level = Integer.parseInt(s);
            int xp = levels.getInt(s + ".xp");
            List<String> commands = levels.getStringList(s + ".commands");
            if(!firework) {
                new Level(xp, level, commands, PickaxesRevamped.getInstance().getConfigValues().getFireworkBuilder());
            } else {
                ConfigurationSection firework = levels.getConfigurationSection(s + ".cosmetics.firework");
                if(firework == null || !firework.getBoolean("enabled")) {
                    continue;
                }
                FireworkBuilder fireworkBuilder = new FireworkBuilder();
                fireworkBuilder.amount(firework.getInt("amount"));
                fireworkBuilder.playerOnly(firework.getBoolean("playerOnly"));
                fireworkBuilder.range(firework.getInt("range"));
                firework.getStringList("colors").forEach(fireworkBuilder::addColor);
                firework.getStringList("fade-colors").forEach(fireworkBuilder::addFadeColor);
                fireworkBuilder.trail(firework.getBoolean("trail"));
                fireworkBuilder.flicker(firework.getBoolean("flicker"));
                new Level(xp,level, commands, fireworkBuilder);
            }
        }
    }
}
