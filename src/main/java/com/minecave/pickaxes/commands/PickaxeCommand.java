package com.minecave.pickaxes.commands;

import com.tadahtech.pub.PickaxesRevamped;
import com.tadahtech.pub.enchant.PEnchant;
import com.tadahtech.pub.enchant.enchants.LuckEnchant;
import com.tadahtech.pub.items.ItemBuilder;
import com.tadahtech.pub.level.Level;
import com.tadahtech.pub.pitem.Pickaxe;
import com.tadahtech.pub.skill.Skill;
import com.tadahtech.pub.sql.PlayerInfo;
import com.tadahtech.pub.utils.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Timothy Andis
 */
public class PickaxeCommand  implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;
        PlayerInfo info = PlayerInfo.get(player);
        if(info == null) {
            Message.FAILURE.sendMessage(player, "You dont have any info!");
            return true;
        }

        List<PEnchant> enchants = new ArrayList<>();
        enchants.add(new LuckEnchant());
        Skill skill = PickaxesRevamped.getInstance().getConfigValues().getEarthquake();
        ItemBuilder builder = ItemBuilder.wrap(new ItemStack(Material.DIAMOND_PICKAXE));
        builder.name(ChatColor.AQUA + "Diamond Pickaxe");

        Pickaxe pickaxe = new Pickaxe(builder.build(), Level.ONE, 0, enchants, ChatColor.AQUA + player.getName() + "'s Diamond Pickaxe: Level: 1 XP: 0", skill);
        player.getInventory().addItem(pickaxe.getItemStack());
        pickaxe.update(player);

        return true;
    }
}
