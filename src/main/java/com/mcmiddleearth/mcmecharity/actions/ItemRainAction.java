package com.mcmiddleearth.mcmecharity.actions;

import com.mcmiddleearth.mcmecharity.CharityPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class ItemRainAction implements Action {

    private final ItemStack item;

    public ItemRainAction(Material material) {
        this.item = new ItemStack(material);
    }

    @Override
    public void execute(String donor, String message, String amount) {
        Player streamer = CharityPlugin.getStreamer();
//Logger.getGlobal().info("Rain "+item.getType());
        List<Item> items = new ArrayList<>();
        if(streamer!=null) {
            new BukkitRunnable() {
                int counter = 0;
                final Random random = new Random();
                @Override
                public void run() {
//Logger.getGlobal().info("Step "+counter);
                    if(counter<180) {
                        Location location = streamer.getLocation().add(0,5,0);
                        for(int i = -10; i<10; i++) {
                            for(int j = -10; j<10; j++) {
                                for(int k = 0; k<4; k+=2) {
                                    if (random.nextDouble() < 0.4) {
                                        items.add(location.getWorld().dropItemNaturally(location.clone().add(i, k, j), item));
                                    }
                                }
                            }
                        }
                    } else if(counter < 380) {
                        if(items.size()>0) {
                            for (int i = 0; i < items.size() * 0.2; i++) {
                                int index = random.nextInt(items.size());
                                Item item = items.get(index);
                                items.remove(index);
                                item.remove();
                            }
                        }
                    } else {
                        items.forEach(Item::remove);
                        cancel();
                    }
                    counter++;
                }
            }.runTaskTimer(CharityPlugin.getInstance(),0,2);
        }
    }
}
