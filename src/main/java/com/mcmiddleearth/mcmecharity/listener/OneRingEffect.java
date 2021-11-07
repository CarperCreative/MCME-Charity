package com.mcmiddleearth.mcmecharity.listener;

import com.mcmiddleearth.mcmecharity.CharityPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class OneRingEffect implements Listener {

    public OneRingEffect() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player streamer = CharityPlugin.getStreamer();
                if(streamer!=null) {
                    if(isOneRing(streamer.getInventory().getItemInMainHand())) {
                        streamer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1, true, false, false));
                        streamer.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Integer.MAX_VALUE, 1, true, false, false));
                        streamer.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true, false, false));
                    } else {
                        boolean found = false;
                        for(ItemStack item: streamer.getInventory().getContents()) {
                            if (isOneRing(item)) found = true;
                        }
                        if(!found) {
                            streamer.removePotionEffect(PotionEffectType.BLINDNESS);
                            streamer.removePotionEffect(PotionEffectType.CONFUSION);
                            streamer.removePotionEffect(PotionEffectType.INVISIBILITY);
                        }
                    }

                }
            }
        }.runTaskTimer(CharityPlugin.getInstance(), 100, 20);
    }

    @EventHandler
    public void onHandItemChange(PlayerItemHeldEvent event) {
//        Logger.getGlobal().info("Hand item change!");
        ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());
//        if (item != null && item.hasItemMeta() && item.getItemMeta().hasCustomModelData())
//            Logger.getGlobal().info("CMD: " + item.getItemMeta().getCustomModelData());
        Player player = event.getPlayer();
        if (isOneRing(player.getInventory().getItem(event.getNewSlot()))) {
//            Logger.getGlobal().info("put on!");
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1, true, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Integer.MAX_VALUE, 1, true, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true, false, false));
        } else if (isOneRing(player.getInventory().getItem(event.getPreviousSlot()))) {
//            Logger.getGlobal().info("put off!");
            player.removePotionEffect(PotionEffectType.BLINDNESS);
            player.removePotionEffect(PotionEffectType.CONFUSION);
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
        }

    }

    private boolean isOneRing(ItemStack item) {
        return item != null && item.hasItemMeta() && item.getItemMeta().hasCustomModelData()
                && item.getItemMeta().getCustomModelData() == 3001;
    }
}