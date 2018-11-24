package com.nohaxjustprogram.tntwand;

import com.massivecraft.factions.P;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TnTWand implements CommandExecutor
{
    private static final ItemStack WAND = new ItemStack(Material.DIAMOND_HOE);

    private Main plugin;

    public TnTWand(Main pl)
    {
        plugin = pl;
        loadItemMeta(WAND);
    }

    private void loadItemMeta(ItemStack toChange)
    {
        ItemMeta meta = toChange.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("tntwand.name")));
        List<String> newLores = new ArrayList<>();
        plugin.getConfig().getStringList("tntwand.lore").forEach(s -> newLores.add(ChatColor.translateAlternateColorCodes('&', s)));
        meta.setLore(newLores);
        toChange.setItemMeta(meta);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        ItemStack newWandWithAmount = null;
        if (args.length > 0)
        {
            try
            {
                newWandWithAmount = new ItemStack(Material.DIAMOND_HOE, Integer.parseInt(args[1]));
                loadItemMeta(newWandWithAmount);
            }
            catch (NumberFormatException e)
            {

            }
        }


        String notenougharguments = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("notenougharguments"));
        String toomanyarguments = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("toomanyarguments"));
        String playernotfound = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("playernotfound"));
        String noPermissions = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("nopermissions"));
        String noSafeSpotFound = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("nosafespotfound"));
        if (sender instanceof ConsoleCommandSender && cmd.getName().equalsIgnoreCase("tntwand"))
        {
            ConsoleCommandSender p = (ConsoleCommandSender) sender;
            if (args == null || args.length == 0 || args.length == 1)
                p.sendMessage(notenougharguments);
            else if (args.length == 2)
            {
                Player target = Bukkit.getPlayer(args[0]);
                if(target == null)
                {
                    p.sendMessage(playernotfound);
                    return true; // Player is not online
                }

                for (int i = 1; i <= Integer.parseInt(args[1]); i++) {
                    if (target.getInventory().firstEmpty() == -1)
                    {
                        if (target.getEnderChest().firstEmpty() == -1)
                        {
                            p.sendMessage(noSafeSpotFound);
                            continue;
                        }
                        target.getEnderChest().addItem(WAND);
                        continue;
                    }
                    target.getInventory().addItem(WAND);
                }
            }
        }
        else if (sender instanceof Player && cmd.getName().equalsIgnoreCase("tntwand"))
        {
            Player p = (Player) sender;
            if (p.hasPermission("tntwand.give") && args.length == 0)
            {
                p.getInventory().addItem(WAND);
            }

            else if (p.hasPermission("tntwand.give.*") || p.hasPermission("tntwand.give.other"))
            {
                if (args == null || args.length == 0 || args.length == 1)
                    p.sendMessage(notenougharguments);
                else if (args.length == 2)
                {
                    Player target = Bukkit.getPlayer(args[0]);
                    if(target == null)
                    {
                        p.sendMessage(playernotfound);
                        return true; // Player is not online
                    }
                    try
                    {
                        for (int i = 1; i <= Integer.parseInt(args[1]); i++) {
                            if (target.getInventory().firstEmpty() == -1)
                            {
                                if (target.getEnderChest().firstEmpty() == -1)
                                {
                                    p.sendMessage(noSafeSpotFound);
                                    if (newWandWithAmount != null)
                                    {
                                        target.getInventory().addItem(newWandWithAmount);
                                        continue;
                                    }


                                }
                                if (newWandWithAmount != null)
                                {
                                    target.getEnderChest().addItem(newWandWithAmount);
                                    continue;
                                }
                            }
                            target.getInventory().addItem(WAND);
                        }
                    }
                    catch (NumberFormatException e)
                    {

                    }
                }

            }
            else
                p.sendMessage(noPermissions);
        }
        return true;
    }
}
