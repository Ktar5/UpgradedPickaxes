package com.minecave.pickaxes;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.minecave.pickaxes.commands.GiveCommand;
import com.minecave.pickaxes.commands.PickCommand;
import com.minecave.pickaxes.commands.PointsCommand;
import com.minecave.pickaxes.commands.SwordCommand;
import com.minecave.pickaxes.drops.DropManager;
import com.minecave.pickaxes.enchant.PEnchant;
import com.minecave.pickaxes.enchant.PEnchantManager;
import com.minecave.pickaxes.item.PItem;
import com.minecave.pickaxes.item.PItemManager;
import com.minecave.pickaxes.level.LevelManager;
import com.minecave.pickaxes.listener.MenuListener;
import com.minecave.pickaxes.listener.PItemListener;
import com.minecave.pickaxes.listener.PlayerListener;
import com.minecave.pickaxes.player.PlayerManager;
import com.minecave.pickaxes.skill.PSkillManager;
import com.minecave.pickaxes.util.config.CustomConfig;
import com.minecave.pickaxes.util.item.ActionBar;
import com.minecave.pickaxes.util.nbt.*;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnhancedPicks extends JavaPlugin {

    @Getter
    private static EnhancedPicks             instance;
    @Getter
    private        Map<String, CustomConfig> configMap;
    @Getter
    private        LevelManager              levelManager;
    @Getter
    private        PSkillManager             pSkillManager;
    @Getter
    private        DropManager               dropManager;
    @Getter
    private        PEnchantManager           pEnchantManager;
    @Getter
    private        PItemManager              pItemManager;
    @Getter
    private        PlayerManager             playerManager;
    @Getter
    private int costPerLevel = 5;

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
        costPerLevel = getConfig("config").get("costPerLevel", Integer.class, 5);

        levelManager = new LevelManager();
        pSkillManager = new PSkillManager();
        dropManager = new DropManager();
        pEnchantManager = new PEnchantManager();
        pItemManager = new PItemManager();
        playerManager = new PlayerManager();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new MenuListener(), this);
        pm.registerEvents(new PItemListener(), this);
        pm.registerEvents(new PlayerListener(), this);

        getCommand("pgive").setExecutor(new GiveCommand());
        getCommand("pick").setExecutor(new PickCommand());
        getCommand("sword").setExecutor(new SwordCommand());
        getCommand("ppoints").setExecutor(new PointsCommand());

        try {
            Class.forName(EPAttributeBuilder.class.getName());
            Class.forName(EPAttribute.class.getName());
            Class.forName(EPAttributes.class.getName());
            Class.forName(EPAttributeStorage.class.getName());
            Class.forName(EPAttributeType.class.getName());
            Class.forName(EPNbtFactory.class.getName());
            Class.forName(EPNBTSerialization.class.getName());
            Class.forName(EPOperation.class.getName());
            Class.forName(EPNbtFactory.NbtCompound.class.getName());
            Class.forName(EPNbtFactory.NbtType.class.getName());
            Class.forName(EPNbtFactory.NbtList.class.getName());
            Class.forName(EPNbtFactory.StreamOptions.class.getName());
            Class.forName(EPNbtFactory.Wrapper.class.getName());
            Class.forName(EPNbtFactory.LoadMethodSkinUpdate.class.getName());
            Class.forName(EPNbtFactory.LoadMethodWorldUpdate.class.getName());
            Class.forName(EPNbtFactory.LoadCompoundMethod.class.getName());
            Class.forName(EPNbtFactory.CachedNativeWrapper.class.getName());
            Class.forName(EPNbtFactory.ConvertedMap.class.getName());
            Class.forName(EPNbtFactory.ConvertedList.class.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this /*your plugin instance*/,
                ListenerPriority.NORMAL, PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.WINDOW_ITEMS) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {
                    PacketContainer packet = event.getPacket().deepClone();
                    StructureModifier<ItemStack> sm = packet
                            .getItemModifier();
                    for (int j = 0; j < sm.size(); j++) {
                        if (sm.getValues().get(j) != null) {
                            ItemStack item = sm.getValues().get(j);
                            ItemMeta itemMeta = item.getItemMeta();
                            if (itemMeta.hasLore()) {
                                List<String> lore = itemMeta.getLore();
                                for (String s : lore) {
                                    s = s.replace(ChatColor.COLOR_CHAR + "", "");
                                    if (s.startsWith("UUID:")) {
                                        PItem<?> pItem = pItemManager.getPItemMap().get(s.replace("UUID:", ""));
                                        if (pItem != null) {
                                            pItem.setItem(item);
                                            for (PEnchant pEnchant : pItem.getEnchants()) {
                                                pEnchant.apply(pItem);
                                            }
                                            pItem.updateMeta();
                                            lore = pItem.getItem().getItemMeta().getLore();
                                        }
                                        break;
                                    }
                                }
                                for (String s : lore) {
                                    s = s.replace(ChatColor.COLOR_CHAR + "", "");
                                    if (s.startsWith("UUID:")) {
                                        lore.remove(s);
                                        break;
                                    }
                                }
                                itemMeta.setLore(lore);
                                item.setItemMeta(itemMeta);

                            }
                        }
                    }
                    event.setPacket(packet);
                }
                if (event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS) {
                    PacketContainer packet = event.getPacket().deepClone();
                    StructureModifier<ItemStack[]> sm = packet
                            .getItemArrayModifier();
                    for (int j = 0; j < sm.size(); j++) {
                        for (int i = 0; i < sm.getValues().size(); i++) {
                            if (sm.getValues().get(j)[i] != null) {
                                ItemStack item = sm.getValues().get(j)[i];
                                ItemMeta itemMeta = item.getItemMeta();
                                if (itemMeta.hasLore()) {
                                    List<String> lore = itemMeta.getLore();
                                    for (String s : lore) {
                                        s = s.replace(ChatColor.COLOR_CHAR + "", "");
                                        if (s.startsWith("UUID:")) {
                                            PItem<?> pItem = pItemManager.getPItemMap().get(s.replace("UUID:", ""));
                                            if (pItem != null) {
                                                pItem.setItem(item);
                                                for (PEnchant pEnchant : pItem.getEnchants()) {
                                                    pEnchant.apply(pItem);
                                                }
                                                pItem.updateMeta();
                                                lore = pItem.getItem().getItemMeta().getLore();
                                            }
                                            break;
                                        }
                                    }
                                    for (String s : lore) {
                                        s = s.replace(ChatColor.COLOR_CHAR + "", "");
                                        if (s.startsWith("UUID:")) {
                                            lore.remove(s);
                                            break;
                                        }
                                    }
                                    item.setItemMeta(itemMeta);
                                }
                            }
                        }
                    }
                    event.setPacket(packet);
                }
            }
        });

        Bukkit.getOnlinePlayers().forEach(playerManager::load);

        getServer().getScheduler().runTaskTimer(this, () -> Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.getItemInHand() != null).forEach(p -> {
                    PItem<?> pItem = pItemManager.getPItem(p.getItemInHand());
                    if (pItem != null) {
                        String displayName = pItem.buildName();
                        ActionBar.sendActionBar(p, displayName);
                    }
                }), 20l, 20l);
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.closeInventory();
            playerManager.save(p);
        });
    }

    public void saveDefaultConfig(String name) {
        String fileName = name + ".yml";
        configMap.put(name, new CustomConfig(getDataFolder(), fileName));
    }

    public CustomConfig getConfig(String name) {
        return configMap.get(name);
    }
}
