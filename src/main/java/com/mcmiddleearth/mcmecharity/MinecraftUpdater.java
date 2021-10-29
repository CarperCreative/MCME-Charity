package com.mcmiddleearth.mcmecharity;

import com.mcmiddleearth.mcmecharity.tiltify.TiltifyConnector;
import org.bukkit.scheduler.BukkitRunnable;

public class MinecraftUpdater extends BukkitRunnable {

    private final TiltifyConnector tiltifyConnector;
    private final RewardManager rewardManager;


    public MinecraftUpdater(TiltifyConnector tiltifyConnector, RewardManager rewardManager) {
        this.tiltifyConnector = tiltifyConnector;
        this.rewardManager = rewardManager;
    }

    @Override
    public void run() {
        tiltifyConnector.getRewards().forEach(reward -> {
            if(!rewardManager.hasRegistered(reward)) {
                rewardManager.register(reward);
                rewardManager.getRegisteredPlayers().forEach( player -> {
                    player.sendMessage("Found new reward: "+reward.getDescription());
                });
            }
        });
    }
}
