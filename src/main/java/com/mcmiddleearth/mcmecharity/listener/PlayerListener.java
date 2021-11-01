package com.mcmiddleearth.mcmecharity.listener;

import com.mcmiddleearth.mcmecharity.CharityPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
}
