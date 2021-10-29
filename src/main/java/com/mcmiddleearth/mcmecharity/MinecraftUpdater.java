package com.mcmiddleearth.mcmecharity;

import com.mcmiddleearth.mcmecharity.managers.ChallengeManager;
import com.mcmiddleearth.mcmecharity.managers.PollManager;
import com.mcmiddleearth.mcmecharity.managers.RewardManager;
import org.bukkit.scheduler.BukkitRunnable;

public class MinecraftUpdater extends BukkitRunnable {

    //private final TiltifyConnector tiltifyConnector;
    private final RewardManager rewardManager;
    private final PollManager pollManager;
    private final ChallengeManager challengeManager;


    public MinecraftUpdater(/*TiltifyConnector tiltifyConnector,*/ RewardManager rewardManager, PollManager pollManager,
                            ChallengeManager challengeManager) {
        //this.tiltifyConnector = tiltifyConnector;
        this.rewardManager = rewardManager;
        this.pollManager = pollManager;
        this.challengeManager = challengeManager;
    }

    @Override
    public void run() {
        /*tiltifyConnector.getRewards().forEach(reward -> {
            if(!rewardManager.hasRegistered(reward)) {
                rewardManager.register(reward);
                rewardManager.getRegisteredPlayers().forEach( player -> {
                    player.sendMessage("Found new reward: "+reward.getDescription());
                });
            }
        });*/
        rewardManager.handleRewards();
        pollManager.handlePolls();
        challengeManager.handleChallenges();
    }
}
