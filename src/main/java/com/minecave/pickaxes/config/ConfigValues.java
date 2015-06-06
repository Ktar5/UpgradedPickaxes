package com.minecave.pickaxes.config;

import com.minecave.pickaxes.PickaxesRevamped;
import com.minecave.pickaxes.drops.BlockDrop;
import com.minecave.pickaxes.drops.BlockValues;
import com.minecave.pickaxes.drops.MobDrop;
import com.minecave.pickaxes.level.Level;
import com.minecave.pickaxes.level.LevelGenerator;
import com.minecave.pickaxes.menu.menus.*;
import com.minecave.pickaxes.skill.skills.Bomber;
import com.minecave.pickaxes.skill.skills.Earthquake;
import com.minecave.pickaxes.skill.skills.Lightning;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

/**
 * @author Timothy Andis
 */
public class ConfigValues {

    private FireworkBuilder fireworkBuilder;
    private String titleLine1, titleLine2;
    private FileConfiguration config;
    private List<Integer> blacklist;
    private MainMenu mainMenu;
    private SkillsMenu skillsMenu;
    private PickMenu pickMenu;
    private SwordMenu swordMenu;
    private UpgradesMenu upgradesMenu;
    private Earthquake earthquake;
    private Lightning lightning;
    private Bomber bomber;

    public ConfigValues(FileConfiguration config) {
        this.config = config;
        load();
        loadDrops();
        loadMenus();
        loadSkills();
    }

    public void load() {
        List<String> levelUpMessage = config.getStringList("level-up-message");
        Level.setLevelUpMessage(levelUpMessage);
        this.titleLine1 = ChatColor.translateAlternateColorCodes('&', config.getString("level-up-title.line1"));
        this.titleLine2 = ChatColor.translateAlternateColorCodes('&', config.getString("level-up-title.line2"));
        FileConfiguration levelConfig = config("levels");
        ConfigurationSection firework = config.getConfigurationSection("cosmetics.firework");
        boolean perLevel = firework.getBoolean("determinePerLevel");
        boolean enabled = firework.getBoolean("enabled");
        new LevelGenerator(levelConfig, perLevel);
        if(enabled) {
            if(!perLevel) {
                this.fireworkBuilder = new FireworkBuilder();
                if (firework.get("blacklist") != null) {
                    this.blacklist = firework.getIntegerList("blacklist");
                }
                this.fireworkBuilder.amount(firework.getInt("amount"));
                this.fireworkBuilder.playerOnly(firework.getBoolean("playerOnly"));
                this.fireworkBuilder.range(firework.getInt("range"));
                firework.getStringList("colors").forEach(this.fireworkBuilder::addColor);
                firework.getStringList("fade-colors").forEach(this.fireworkBuilder::addFadeColor);
                this.fireworkBuilder.trail(firework.getBoolean("trail"));
                this.fireworkBuilder.flicker(firework.getBoolean("flicker"));
            }
        }
    }

    private void loadDrops() {
        FileConfiguration config = config("drops");
        ConfigurationSection blocks = config.getConfigurationSection("blocks");
        for(String s : blocks.getKeys(false)) {
            ConfigurationSection section = blocks.getConfigurationSection(s);
            for(String l : section.getKeys(false)) {
                ConfigurationSection levels = section.getConfigurationSection("levels");
                for(String d : levels.getKeys(false)) {
                    List<String> drops = levels.getStringList(d + ".drops");
                    for(String raw : drops) {
                        String[] str = raw.split("%");
                        String command = str[0];
                        int weight = Integer.parseInt(str[1]);
                        new BlockDrop(weight, command, Integer.parseInt(d));
                    }
                }
            }
        }
        blocks = config.getConfigurationSection("mobs");
        for(String s : blocks.getKeys(false)) {
            ConfigurationSection section = blocks.getConfigurationSection(s);
            for(String l : section.getKeys(false)) {
                ConfigurationSection levels = section.getConfigurationSection("levels");
                for(String d : levels.getKeys(false)) {
                    List<String> drops = levels.getStringList(d + ".drops");
                    for(String raw : drops) {
                        String[] str = raw.split("%");
                        String command = str[0];
                        int weight = Integer.parseInt(str[1]);
                        new MobDrop(weight, command, Integer.parseInt(d));
                    }
                }
            }
        }
    }

    public void loadMenus() {
        FileConfiguration config = config("menus");
        this.mainMenu = new MainMenu(color(config.getString("mainMenu")));
        this.skillsMenu = new SkillsMenu(color(config.getString("skillsMenu")));
        this.upgradesMenu = new UpgradesMenu(color(config.getString("upgradeMnu")));
        this.pickMenu = new PickMenu(color(config.getString("pickMenu")));
        this.swordMenu = new SwordMenu(color(config.getString("swordMenu")));
    }

    public void loadSkills() {
        FileConfiguration config = config("skills");
        ConfigurationSection eq = config.getConfigurationSection("earthquake");
        this.earthquake = new Earthquake(color(eq.getString("name")), eq.getInt("cooldown"), eq.getInt("level"));
        ConfigurationSection tnt = config.getConfigurationSection("tnt");
        this.bomber = new Bomber(color(tnt.getString("name")), tnt.getInt("cooldown"), tnt.getInt("level"),
          tnt.getInt("maxBlocks"),  tnt.getInt("fuse"), tnt.getBoolean("toSeconds"));
        ConfigurationSection light = config.getConfigurationSection("lightning");
        this.lightning = new Lightning(color(light.getString("name")), light.getInt("cooldown"), light.getInt("level"),
          light.getInt("depth"), light.getInt("distance"));
    }

    public void loadXp() {
        FileConfiguration config = config("xp");
        ConfigurationSection section = config.getConfigurationSection("blocks");
        for(String s : section.getKeys(false)) {
            Material material = Material.matchMaterial(s);
            int xp = section.getInt(s);
            new BlockValues(xp, material);
        }
    }

    private FileConfiguration config(String file) {
        return YamlConfiguration.loadConfiguration(new File(PickaxesRevamped.getInstance().getDataFolder(), file + ".yml"));
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public void reload() {
        load();
    }

    public FireworkBuilder getFireworkBuilder() {
        return fireworkBuilder;
    }

    public String getTitleLine1() {
        return titleLine1;
    }

    public String getTitleLine2() {
        return titleLine2;
    }

    public List<Integer> getBlacklist() {
        return blacklist;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public MainMenu getMainMenu() {
        return mainMenu;
    }

    public SkillsMenu getSkillsMenu() {
        return skillsMenu;
    }

    public PickMenu getPickMenu() {
        return pickMenu;
    }

    public SwordMenu getSwordMenu() {
        return swordMenu;
    }

    public UpgradesMenu getUpgradesMenu() {
        return upgradesMenu;
    }

    public Earthquake getEarthquake() {
        return earthquake;
    }

    public Lightning getLightning() {
        return lightning;
    }

    public Bomber getBomber() {
        return bomber;
    }
}
