package com.mcmiddleearth.mcmecharity.listener;

import com.mcmiddleearth.mcmecharity.CharityPlugin;
import com.mcmiddleearth.mcmecharity.actions.InventoryReplaceAction;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.logging.Logger;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent event) {
        Player streamer = CharityPlugin.getStreamer();
        if(event.getPlayer().equals(streamer) && event.getItem().getType().equals(Material.COOKIE)) {
            int duration = 400;
            try {
                duration = Integer.parseInt(CharityPlugin.getConfigString("lembas_duration"));
            } catch(NumberFormatException ignore) {}
            streamer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration,1,
                                               true,false,false));
        }
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        if(event.getEntity().getItemStack().getType().name().endsWith("_BED")
            || event.getEntity().getItemStack().getType().name().endsWith("_BOAT")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBedCraft(CraftItemEvent event) {
        ItemStack item = event.getInventory().getResult();
        if(item!=null && (item.getType().name().endsWith("_BED")
                       || item.getType().name().endsWith("_BOAT"))) {
            event.setCancelled(true);
        }
    }

}
