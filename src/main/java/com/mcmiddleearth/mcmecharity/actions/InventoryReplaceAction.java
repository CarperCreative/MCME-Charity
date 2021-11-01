package com.mcmiddleearth.mcmecharity.actions;

import com.mcmiddleearth.mcmecharity.CharityPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class InventoryReplaceAction implements Action, Listener {

    private final Material material;

    private ItemStack[] storedInventory;
    private boolean replaced;
    private int eaten;

    public InventoryReplaceAction(Material material) {
        this.material = material;
    }

    @Override
    public void execute(String donor, String message, String amount) {
        Player streamer = CharityPlugin.getStreamer();
        if(streamer!=null) {
            if(!replaced) {
                storedInventory = streamer.getInventory().getContents();
                Bukkit.getPluginManager().registerEvents(this,CharityPlugin.getInstance());
            }
            replaced = true;
            eaten = 0;
            ItemStack[] replacement = Arrays.copyOf(storedInventory, storedInventory.length);
            for(int i = 0; i<replacement.length; i++) {
                replacement[i] = new ItemStack(material);
            }
            streamer.getInventory().setContents(replacement);
        }
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
        Player streamer = CharityPlugin.getStreamer();
        if(streamer!=null && replaced && event.getItem().getType().equals(material)) {
            eaten++;
            if(eaten == 3) {
                streamer.getInventory().setContents(storedInventory);
                HandlerList.unregisterAll(this);
                replaced = false;
            }
        }
    }
}
