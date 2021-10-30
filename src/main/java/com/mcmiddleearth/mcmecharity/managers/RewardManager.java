package com.mcmiddleearth.mcmecharity.managers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mcmiddleearth.mcmecharity.CharityPlugin;
import com.mcmiddleearth.mcmecharity.Donation;
import com.mcmiddleearth.mcmecharity.actions.Action;
import com.mcmiddleearth.mcmecharity.actions.ActionCompiler;
import com.mcmiddleearth.mcmecharity.incentives.Reward;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RewardManager {

    //private final Set<Player> registeredPlayers = new HashSet<>();

    private final Map<Integer, Reward> registeredRewards = new HashMap<>();

    private final Set<Donation> donations = new HashSet<>();

    private static final String KEY_REWARD = "reward",
                                KEY_DONATION = "donation";

    /*public void addPlayer(Player player) {
        registeredPlayers.add(player);
    }

    public void removePlayer(Player player) {
        registeredPlayers.remove(player);
    }

    public Set<Player> getRegisteredPlayers() { return registeredPlayers;}
    */

    /*public boolean hasRegistered(Reward reward) {
        //Logger.getGlobal().info("New: "+reward.getDescription());
        //Logger.getGlobal().info("Registered:" );
        //registeredRewards.forEach(rew -> Logger.getGlobal().info(rew.getDescription()));
        //Logger.getGlobal().info("Contains: "+registeredRewards.contains(reward));
        return registeredRewards.contains(reward);
    }*/

    /*public void register(Reward reward) {
        registeredRewards.add(reward);
    }*/

    public synchronized void updateRewards(String rewardData) {
        registeredRewards.clear();
        Gson gson = new Gson();
        JsonElement rewardDataJson =  JsonParser.parseString(rewardData);
        JsonArray rewardListJson = rewardDataJson.getAsJsonObject().get("data").getAsJsonArray();
//Logger.getGlobal().info("size: "+rewardListJson.size());
        for(int i = 0; i< rewardListJson.size(); i++) {
            Reward reward = gson.fromJson(rewardListJson.get(i), Reward.class);
//Logger.getGlobal().info("reward: "+reward.getDescription());
            Action action = ActionCompiler.compile(KEY_REWARD, reward.getId());
            if(action!=null) {
                reward.setAction(action);
                registeredRewards.put(reward.getId(), reward);
            }
        }
    }

    public synchronized void handleRewards() {
        try {
            donations.stream().filter(donation -> !donation.isHandled()).forEach(donation -> {
                if (donation.getReward() != null && donation.getReward().getAction() != null) {
                    donation.getReward().getAction().execute(donation.getName(),donation.getComment(),""+donation.getAmount());
                    donation.setHandled(true);
                    CharityPlugin.setStorage(KEY_DONATION, ""+donation.getId(), true, false);
                }
            });
        } finally {
            CharityPlugin.saveStorage();
        }
    }

    public synchronized void updateDonations(String donationData) {
        Gson gson = new Gson();
        JsonElement donationDataJson =  JsonParser.parseString(donationData);
        JsonArray donationListJson = donationDataJson.getAsJsonObject().get("data").getAsJsonArray();
        Set<Donation> recentDonations = new HashSet<>();
        for(int i = 0; i< donationListJson.size(); i++) {
            Donation donation = gson.fromJson(donationListJson.get(i), Donation.class);
            boolean isHandled = CharityPlugin.getStorage(KEY_DONATION,""+donation.getId());
            if(!isHandled) {
                int rewardId = donation.getRewardId();
                Reward reward = registeredRewards.get(rewardId);
                if(reward!=null) {
                    donation.setReward(reward);
                    recentDonations.add(donation);
                }
            }
        }
        Set<Donation> removal = new HashSet<>();
        donations.stream().filter(donation -> donation.isHandled() && !recentDonations.contains(donation))
                 .forEach(removal::add);
        donations.removeAll(removal);
        donations.addAll(recentDonations);
    }
}
