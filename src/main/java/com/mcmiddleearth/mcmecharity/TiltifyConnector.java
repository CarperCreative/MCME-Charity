package com.mcmiddleearth.mcmecharity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mcmiddleearth.mcmecharity.incentives.Reward;

import java.util.HashSet;
import java.util.Set;

public class TiltifyConnector {

    private final Set<Donation> recentDonations = new HashSet<>();
    private final Set<Donation> allDonations = new HashSet<>();

    private final Set<Reward> rewards = new HashSet<>();

    public synchronized Set<Donation> getRecentDonations() {
        return new HashSet<>(recentDonations);
    }

    public synchronized Set<Donation> getAllDonations() {
        return new HashSet<>(allDonations);
    }

    public synchronized Set<Reward> getRewards() {
        return new HashSet<>(rewards);
    }

    public synchronized void handleRewardUpdate(String rewardData) {
        Gson gson = new Gson();
        JsonElement rewardDataJson =  new JsonParser().parse(rewardData);
        JsonArray rewardListJson = rewardDataJson.getAsJsonObject().get("data").getAsJsonArray();
//Logger.getGlobal().info("size: "+rewardListJson.size());
        for(int i = 0; i< rewardListJson.size(); i++) {
            Reward reward = gson.fromJson(rewardListJson.get(i), Reward.class);
//Logger.getGlobal().info("reward: "+reward.getDescription());
            rewards.add(reward);
        }
    }
}
