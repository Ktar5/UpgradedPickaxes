package com.minecave.pickaxes.config;

import com.minecave.pickaxes.PickaxesRevamped;
import com.minecave.pickaxes.builder.FireworkBuilder;
import com.minecave.pickaxes.drops.BlockDrop;
import com.minecave.pickaxes.drops.BlockValues;
import com.minecave.pickaxes.drops.MobDrop;
import com.minecave.pickaxes.level.Level;
import com.minecave.pickaxes.level.LevelGenerator;
import com.minecave.pickaxes.menu.menus.*;
import com.minecave.pickaxes.skill.Skills;
import com.minecave.pickaxes.skill.skills.Bomber;
import com.minecave.pickaxes.skill.skills.Earthquake;
import com.minecave.pickaxes.skill.skills.Ice;
import com.minecave.pickaxes.skill.skills.Lightning;
import com.minecave.pickaxes.skill.skills.sword.Acid;
import com.minecave.pickaxes.skill.skills.sword.FireBallSkill;
import com.minecave.pickaxes.skill.skills.sword.Rain;
import com.minecave.pickaxes.skill.skills.sword.Shotgun;
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
    private FileConfiguration enchants;
    private List<Integer> blacklist;
    private MainPickMenu mainPickMenu;
    private MainSwordMenu mainSwordMenu;
    private SkillsMenu skillsMenu;
    private PickMenu pickMenu;
    private SwordMenu swordMenu;
    private UpgradesMenu upgradesMenu;


    private Earthquake earthquake;
    private Lightning lightning;
    private Ice ice;
    private Bomber bomber;
    private Shotgun shotgun;
    private Acid acid;
    private FireBallSkill fireball;
    private Rain rain;

    public ConfigValues(FileConfiguration config) {
        this.config = config;
    }

    public void init() {
        load();
        loadDrops();
        loadMenus();
        loadSkills();
        loadXp();
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
        if (enabled) {
            if (!perLevel) {
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
            new LevelGenerator(levelConfig, perLevel);
        }

        enchants = config("enchants");

    }

    private void loadDrops() {
        FileConfiguration config = config("drops");
        ConfigurationSection blocks = config.getConfigurationSection("blocks");
        for (String s : blocks.getKeys(false)) {
            ConfigurationSection section = blocks.getConfigurationSection(s);
            for (String l : section.getKeys(false)) {
                ConfigurationSection levels = section.getConfigurationSection("levels");
                for (String d : levels.getKeys(false)) {
                    List<String> drops = levels.getStringList(d + ".drops");
                    for (String raw : drops) {
                        String[] str = raw.split("%");
                        String command = str[0];
                        int weight = Integer.parseInt(str[1]);
                        new BlockDrop(weight, command, Integer.parseInt(d));
                    }
                }
            }
        }
        blocks = config.getConfigurationSection("mobs");
        for (String s : blocks.getKeys(false)) {
            ConfigurationSection section = blocks.getConfigurationSection(s);
            for (String l : section.getKeys(false)) {
                ConfigurationSection levels = section.getConfigurationSection("levels");
                for (String d : levels.getKeys(false)) {
                    List<String> drops = levels.getStringList(d + ".drops");
                    for (String raw : drops) {
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
        this.mainPickMenu = new MainPickMenu(color(config.getString("mainPickMenu")));
        this.mainSwordMenu = new MainSwordMenu(color(config.getString("mainSwordMenu")));
        this.skillsMenu = new SkillsMenu(color(config.getString("skillsMenu")));
        this.upgradesMenu = new UpgradesMenu(color(config.getString("upgradeMenu")));
        this.pickMenu = new PickMenu(color(config.getString("pickaxeMenu")));
        this.swordMenu = new SwordMenu(color(config.getString("swordMenu")));
    }

    public void loadSkills() {
        FileConfiguration config = config("skills");

        ConfigurationSection eq = config.getConfigurationSection("earthquake");
        this.earthquake = new Earthquake(eq.getInt("radius"), color(eq.getString("name")), eq.getInt("cooldown"), eq.getInt("levelUnlocked"), eq.getInt("cost"), eq.getString("permission"));
        Skills.add("earthquake", this.earthquake);

        ConfigurationSection ice = config.getConfigurationSection("ice");
        this.ice = new Ice(color(ice.getString("name")), ice.getInt("cooldown"), ice.getInt("levelUnlocked"), ice.getInt("cost"), ice.getString("permission"), ice.getInt("radius"));
        Skills.add("ice", this.ice);

        ConfigurationSection tnt = config.getConfigurationSection("tnt");
        this.bomber = new Bomber(color(tnt.getString("name")), tnt.getInt("cooldown"), tnt.getInt("levelUnlocked"), tnt.getInt("cost"), tnt.getString("permission"),
                tnt.getInt("maxBlocks"), tnt.getInt("fuse"), tnt.getBoolean("toSeconds"));
        Skills.add("bomber", this.bomber);

        ConfigurationSection light = config.getConfigurationSection("lightning");
        this.lightning = new Lightning(color(light.getString("name")), light.getInt("cooldown"), light.getInt("levelUnlocked"), light.getInt("cost"), light.getString("permission"),
                light.getInt("depth"), light.getInt("distance"));
        Skills.add("lightning", this.lightning);

        ConfigurationSection shot = config.getConfigurationSection("shotgun");
        this.shotgun = new Shotgun(color(shot.getString("name")), shot.getInt("cooldown"),
                shot.getInt("levelUnlocked"), shot.getInt("cost"), shot.getString("permission"),
                shot.getInt("numberOfSnowballs"));
        Skills.add("shotgun", this.shotgun);

        ConfigurationSection acidC = config.getConfigurationSection("acid");
        this.acid = new Acid(color(acidC.getString("name")), acidC.getInt("cooldown"),
                acidC.getInt("levelUnlocked"), acidC.getInt("cost"), acidC.getString("permission"),
                acidC.getInt("numberOfAcidParts"), acidC.getInt("radiusPerHit"));
        Skills.add("acid", this.acid);

        ConfigurationSection r = config.getConfigurationSection("rain");
        this.rain = new Rain(color(r.getString("name")), r.getInt("cooldown"),
                r.getInt("levelUnlocked"), r.getInt("cost"), r.getString("permission"),
                r.getInt("arrowHeight"),r.getInt("arrowCount"),r.getInt("seconds"));
        Skills.add("rain", this.rain);

        ConfigurationSection fire = config.getConfigurationSection("fireball");
        this.fireball = new FireBallSkill(color(fire.getString("name")), fire.getInt("cooldown"),
                fire.getInt("levelUnlocked"), fire.getInt("cost"), fire.getString("permission"));
        Skills.add("fireball", this.fireball);
    }

    public void loadXp() {
        FileConfiguration config = config("xp");
        ConfigurationSection section = config.getConfigurationSection("blocks");
        for (String s : section.getKeys(false)) {
            Material material = Material.matchMaterial(s);
            int xp = section.getInt(s);
            new BlockValues(xp, material);
        }
    }

    private FileConfiguration config(String file) {
        file += ".yml";
        File f = new File(PickaxesRevamped.getInstance().getDataFolder(), file);
        if (!f.exists()) {
            PickaxesRevamped.getInstance().saveResource(file, true);
        }
        return YamlConfiguration.loadConfiguration(f);
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

    public MainPickMenu getMainPickMenu() {
        return mainPickMenu;
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

    public Ice getIce() {
        return ice;
    }

    public Bomber getBomber() {
        return bomber;
    }

    public Shotgun getShotgun() {
        return shotgun;
    }

    public Acid getAcid() {
        return acid;
    }
    
    public Rain getRain() {
        return rain;
    }
    
    public FireBallSkill getFireball() {
        return fireball;
    }

    public MainSwordMenu getMainSwordMenu() {
        return mainSwordMenu;
    }

    public FileConfiguration getEnchants() {
        return enchants;
    }
}
