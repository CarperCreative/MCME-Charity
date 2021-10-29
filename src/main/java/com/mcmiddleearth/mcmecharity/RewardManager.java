package com.mcmiddleearth.mcmecharity;

import com.mcmiddleearth.mcmecharity.tiltify.incentives.Reward;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

public class RewardManager {

    private final Set<Player> registeredPlayers = new HashSet<>();

    private final Set<Reward> registeredRewards = new HashSet<>();

    public void addPlayer(Player player) {
        registeredPlayers.add(player);
    }

    public void removePlayer(Player player) {
        registeredPlayers.remove(player);
    }

    public Set<Player> getRegisteredPlayers() { return registeredPlayers;}


    public boolean hasRegistered(Reward reward) {
        //Logger.getGlobal().info("New: "+reward.getDescription());
        //Logger.getGlobal().info("Registered:" );
        //registeredRewards.forEach(rew -> Logger.getGlobal().info(rew.getDescription()));
        //Logger.getGlobal().info("Contains: "+registeredRewards.contains(reward));
        return registeredRewards.contains(reward);
    }

    public void register(Reward reward) {
        registeredRewards.add(reward);
    }
}
