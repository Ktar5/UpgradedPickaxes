package com.minecave.pickaxes.commands;

import com.minecave.pickaxes.PickaxesRevamped;
import com.minecave.pickaxes.enchant.PEnchant;
import com.minecave.pickaxes.enchant.enchants.LuckEnchant;
import com.minecave.pickaxes.items.ItemBuilder;
import com.minecave.pickaxes.level.Level;
import com.minecave.pickaxes.pitem.Pickaxe;
import com.minecave.pickaxes.skill.Skill;
import com.minecave.pickaxes.sql.PlayerInfo;
import com.minecave.pickaxes.utils.Message;
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
