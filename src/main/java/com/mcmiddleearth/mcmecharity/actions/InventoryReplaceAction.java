package com.mcmiddleearth.mcmecharity.actions;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.PersistentDataKey;
import com.mcmiddleearth.mcmecharity.CharityPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class InventoryReplaceAction implements Action, Listener {

    private final Material material;

    private ItemStack[] storedInventory;
    private boolean replaced;
    private int eaten;

    private static final File inventoryFolder = new File(CharityPlugin.getInstance().getDataFolder(),"inventory_storage");

    public InventoryReplaceAction(Material material) {
        this.material = material;
        if(!inventoryFolder.exists()) {
            Logger.getLogger(this.getClass().getSimpleName()).info("Make inventory Folder: "+inventoryFolder.mkdir());
        }
    }

    @Override
    public String getCooldownGroupName() {
        return null;
    }

    @Override
    public void execute(String donor, String message, String amount) {
        Player streamer = CharityPlugin.getStreamer();
        if(streamer!=null) {
            if(!replaced) {
                storedInventory = streamer.getInventory().getContents();
                saveInventory();
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
            if(eaten >= 3) {
                streamer.getInventory().setContents(storedInventory);
                HandlerList.unregisterAll(this);
                replaced = false;
            }
        }
    }

    private void saveInventory() {
        int index = inventoryFolder.listFiles().length;
        File file;
        do {
            index++;
            file = new File(inventoryFolder, "inventory_" + index);
        } while(file.exists());
        YamlConfiguration config = new YamlConfiguration();
        ItemStack[] temp = Arrays.copyOf(storedInventory,storedInventory.length);
        for(int i = 0; i < temp.length; i++) {
            if(temp[i] !=null) {
                ItemMeta meta = temp[i].getItemMeta();
                if(meta!=null) {
                    NamespacedKey key = EntitiesPlugin.getInstance().getPersistentDataKey(PersistentDataKey.ITEM_REMOVAL_TIME);
                    if (meta.getPersistentDataContainer().has(key, PersistentDataType.PrimitivePersistentDataType.LONG)) {
                        temp[i] = null;
Logger.getGlobal().info("Remove item: "+i);
                    }
                }
            }
        }
        config.set("items",temp);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeInt(storedInventory.length);
            for(ItemStack item: storedInventory) {
                out.writeObject(item);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public static void loadInventory(int back) {
        Player streamer = CharityPlugin.getStreamer();
        if(streamer != null) {
            int index = inventoryFolder.listFiles().length - back;
            File file = new File(inventoryFolder, "inventory_" + index);
            /*try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                int size = in.readInt();
                ItemStack[] items = new ItemStack[size];
                for (int i = 0; i < size; i++) {
                    items[i] = (ItemStack) in.readObject();
                }
                streamer.getInventory().setContents(items);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }*/
            YamlConfiguration config = new YamlConfiguration();
            try {
                config.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
            @SuppressWarnings("unchecked")
            ArrayList<ItemStack> temp = (ArrayList<ItemStack>) config.get("items");
            if(temp!=null)
                streamer.getInventory().setContents(temp.toArray(new ItemStack[0]));
        }
    }
}
