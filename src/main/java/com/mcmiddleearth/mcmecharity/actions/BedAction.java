package com.mcmiddleearth.mcmecharity.actions;

import com.mcmiddleearth.mcmecharity.CharityPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BedAction implements Action {

    @Override
    public void execute(String donor, String message, String amount) {
        Player streamer = CharityPlugin.getStreamer();
        if(streamer!=null) {
            Location loc = streamer.getLocation().clone();
            loc.setPitch(0);
            loc.add(loc.getDirection().multiply(2));
            Block block = loc.getBlock();
            /*while(block.isPassable() && block.getY()<block.getWorld().getMaxHeight()) {
                block = block.getRelative(BlockFace.UP);
            }*/
            for(int i = -1; i< 2; i++) {
                for(int j = -1; j< 2; j++) {
                    for(int k = -1; k < 3; k++) {
                        if(k==-1) {
                           block.getRelative(i,-1,j).setType(Material.OAK_PLANKS);
                        } else {
                            block.getRelative(i,-1,j).setType(Material.AIR);
                        }
                    }
                }
            }
            block.setType(Material.RED_BED);
        }
    }
}
