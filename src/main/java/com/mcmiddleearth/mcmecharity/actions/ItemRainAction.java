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

public class ItemRainAction implements Action {

    private final ItemStack item;

    public ItemRainAction(Material material) {
        this.item = new ItemStack(material);
    }

    @Override
    public void execute(String donor, String message, String amount) {
        Player streamer = CharityPlugin.getStreamer();
        List<Item> items = new ArrayList<>();
        if(streamer!=null) {
            Location location = streamer.getLocation().add(0,10,0);
            new BukkitRunnable() {
                int counter = 0;
                final Random random = new Random();
                @Override
                public void run() {
                    if(counter<40) {
                        for(int i = -5; i<5; i++) {
                            for(int j = -5; j<5; j++) {
                                if(random.nextDouble()<0.5) {
                                    items.add(location.getWorld().dropItemNaturally(location.clone().add(i,0,j),item));
                                }
                            }
                        }
                    } else if(counter < 80) {
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
            }.runTaskTimer(CharityPlugin.getInstance(),0,5);
        }
    }
}
