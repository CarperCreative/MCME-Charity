package com.mcmiddleearth.mcmecharity.actions;

import com.mcmiddleearth.mcmecharity.CharityPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;

public class BedAction implements Action {
    @Override
    public String getCooldownGroupName() {
        return "bed";
    }

    @Override
    public void execute(String donor, String message, String amount) {
        Player streamer = CharityPlugin.getStreamer();
        if(streamer!=null) {
            Location loc = streamer.getLocation().clone();
            loc.setPitch(0);
            loc.add(loc.getDirection().multiply(1));
            BlockFace facing;
            if(loc.getYaw()<-110 || loc.getYaw()>160) facing = BlockFace.EAST;
            else if(loc.getYaw()<-20) facing = BlockFace.SOUTH;
            else if(loc.getYaw()<70) facing = BlockFace.WEST;
            else facing = BlockFace.NORTH;
            Block block = loc.getBlock();
            /*while(block.isPassable() && block.getY()<block.getWorld().getMaxHeight()) {
                block = block.getRelative(BlockFace.UP);
            }*/
            /*for(int i = -1; i< 2; i++) {
                for(int j = -1; j< 2; j++) {
                    for(int k = -1; k < 3; k++) {
                        if(k==-1) {
                           block.getRelative(i,k,j).setType(Material.OAK_PLANKS);
                        } else {
                            block.getRelative(i,k,j).setType(Material.AIR);
                        }
                    }
                }
            }*/
            Bed bedData = (Bed) Bukkit.createBlockData(Material.RED_BED);
            bedData.setPart(Bed.Part.FOOT);
            bedData.setFacing(facing);
            block.setBlockData(bedData,false);
            bedData.setPart(Bed.Part.HEAD);
            block.getRelative(facing).setBlockData(bedData,false);
            for(int i = 1; i< 3; i++) {
                for(int j = 0; j< 2; j++) {
                   block.getRelative(0,i,j).setType(Material.AIR);
                }
            }
        }
    }
}
