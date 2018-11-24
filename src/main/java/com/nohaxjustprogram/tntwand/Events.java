package com.nohaxjustprogram.tntwand;

import com.google.common.collect.Sets;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.Set;

public class Events implements Listener
{
    private Set<Location> blocksBroke = Sets.newHashSet();
    private Set<InventoryView> openedInvs = Sets.newHashSet();
    private int size = 0;
    private Main plugin;
    public Events(Main pl)
    {
        plugin = pl;
    }



    @EventHandler
    public void onInvOpen(InventoryOpenEvent view)
    {
        if (!openedInvs.contains(view.getPlayer().getOpenInventory()))
            return;

        openedInvs.remove(view.getPlayer().getOpenInventory());
        view.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent ev)
    {
        if(!blocksBroke.contains(ev.getBlock().getLocation()))
            return;
        FPlayers fplayers = FPlayers.getInstance();
        FPlayer fplayer = fplayers.getByPlayer(ev.getPlayer().getPlayer());
        Faction faction = null;
        if (fplayer.hasFaction())
            faction = fplayer.getFaction();
        if (!FactionsBlockListener.playerCanBuildDestroyBlock(ev.getPlayer().getPlayer(), ev.getBlock().getLocation(), "destroy", false))
            ev.setCancelled(true);
        blocksBroke.remove(ev.getBlock().getLocation());
        ev.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        Action action = event.getAction();
        Block block = event.getClickedBlock();
        Chest chest;
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getItemInHand();
        ItemMeta im = itemInHand.getItemMeta();
        String displayname = ChatColor.translateAlternateColorCodes( '&', plugin.getConfig().getString("tntwand.name"));
        String successMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("successmessage"));
        String notinfaction = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("notinfaction"));
        String cannotAccessChest = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("cannotAccessChest"));

        Faction faction = null;
        FPlayers fplayers = FPlayers.getInstance();
        FPlayer fplayer = fplayers.getById(player.getUniqueId().toString());
        if (!itemInHand.getType().equals(Material.AIR))
        {
            if (im.hasDisplayName() && itemInHand.getType().equals(Material.DIAMOND_HOE))
            {
                if ((action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_BLOCK))
                        && (block.getType().equals(Material.CHEST) || block.getType().equals(Material.TRAPPED_CHEST)) && im.getDisplayName().equalsIgnoreCase(displayname))
                {
                    BlockBreakEvent ev = new BlockBreakEvent(block, player);
                    InventoryOpenEvent view = new InventoryOpenEvent(player.getOpenInventory());
                    openedInvs.add(player.getOpenInventory());
                    if (ev.getPlayer().getGameMode().equals(GameMode.CREATIVE))
                    {
                        blocksBroke.add(ev.getBlock().getLocation());
                    }

                    if (fplayer.hasFaction())
                    {
                        faction = fplayer.getFaction();
                        FLocation flocation = new FLocation(block);

                        Set<FLocation> factionClaims = faction.getAllClaims();

                        if (factionClaims.contains(flocation))
                        {
                            chest = (Chest) block.getState();

                            Inventory invToCheck = chest.getInventory();
                            ItemStack tnt = null;

                            if (chest.getBlockInventory().contains(Material.TNT))
                            {
                                ItemStack[] contents = invToCheck.getContents();
                                if (contents == null)
                                {
                                    player.sendMessage("Contents is null");
                                    return;
                                }
                                int[] itemSlot = new int[55];
                                int count = 0;
                                int amount = 0;
                                for (int i = 0; i < contents.length; i++)
                                {
                                    if (contents[i] == null)
                                    {
                                        continue;
                                    }
                                    if (contents[i].getType().equals(Material.TNT))
                                    {
                                        tnt = contents[i];
                                        itemSlot[count] = i;
                                        amount += tnt.getAmount();
                                        count++;
                                        invToCheck.remove(tnt);

                                    }
                                }

                                successMessage = successMessage.replace("%amount%", Integer.toString(amount));
                                faction.addTnt(amount);
                                player.sendMessage(successMessage);
                            }
                        }
                        else
                            player.sendMessage(cannotAccessChest);
                    }
                    else
                        player.sendMessage(notinfaction);
                }
            }
        }
    }
}
