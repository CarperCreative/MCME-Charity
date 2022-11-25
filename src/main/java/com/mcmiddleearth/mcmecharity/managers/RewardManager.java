package com.mcmiddleearth.mcmecharity.managers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mcmiddleearth.mcmecharity.CharityPlugin;
import com.mcmiddleearth.mcmecharity.Donation;
import com.mcmiddleearth.mcmecharity.actions.Action;
import com.mcmiddleearth.mcmecharity.actions.ActionCompiler;
import com.mcmiddleearth.mcmecharity.actions.ScriptAction;
import com.mcmiddleearth.mcmecharity.incentives.Reward;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class RewardManager {

    //private final Set<Player> registeredPlayers = new HashSet<>();

    private final Map<Integer, Reward> registeredRewards = new HashMap<>();

    private final List<Donation> donations = new ArrayList<>();

    private static final String KEY_REWARD = "reward",
                                KEY_DONATION = "donation";

    private final JsonParser jsonParser = new JsonParser();
    private final Map<String, RewardCooldown> scriptCooldowns = new HashMap<>();

    private int donationCooldown = 0, maxDonationCooldown = 20; //

    public synchronized void updateRewards(String rewardData) {
        registeredRewards.clear();

        Gson gson = new Gson();
        JsonElement rewardDataJson =  jsonParser.parse(rewardData);
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
        if (CharityPlugin.getStreamer() == null) {
            return;
        }

        for (final Donation donation : this.donations) {
            if (donation.isHandled() || donation.getReward() == null || donation.getReward().getAction() == null) {
                continue;
            }

            final Reward reward = donation.getReward();
            final Action action = reward.getAction();

            if (action instanceof ScriptAction) {
                final ScriptAction scriptAction = (ScriptAction) action;
                final String script = scriptAction.getScript();

                final RewardCooldown cooldown = this.scriptCooldowns.get(script);
                if (cooldown != null) {
                    if (cooldown.isActive()) {
                        continue;
                    }

                    this.giveReward(donation);
                    cooldown.reset();

                    // End iteration here to prevent running the reward action twice and affecting global cooldown
                    continue;
                }
            }

            if(this.donationCooldown > 0) break;

            this.giveReward(donation);
            this.donationCooldown = this.maxDonationCooldown;
            break;
        }

        CharityPlugin.saveStorage();

        donationCooldown = Math.max(0,--donationCooldown);

        for (final RewardCooldown cooldown : this.scriptCooldowns.values()) {
            cooldown.decrement();
        }

        Logger.getLogger(this.getClass().getSimpleName()).info("Donation queue size: " + donations.size() + " - Cooldown: " + donationCooldown);
    }

    private void giveReward(Donation donation) {
        Logger.getLogger(RewardManager.class.getSimpleName()).info("Donation reward: " + donation.getName());
        donation.getReward().getAction().execute(donation.getName(), donation.getComment(), "" + donation.getAmount());
        donation.setHandled(true);
        CharityPlugin.setStorage(KEY_DONATION, "" + donation.getId(), true, false);
    }

    public synchronized void updateDonations(String donationData) {
//Logger.getGlobal().info("Donation data string: " + donationData);
        Gson gson = new Gson();
        JsonElement donationDataJson = jsonParser.parse(donationData);
        if(donationDataJson instanceof JsonObject && donationDataJson.getAsJsonObject().has("data")) {
            JsonArray donationListJson = donationDataJson.getAsJsonObject().get("data").getAsJsonArray();
            Set<Donation> recentDonations = new HashSet<>();
            for (int i = 0; i < donationListJson.size(); i++) {
                Donation donation = gson.fromJson(donationListJson.get(i), Donation.class);
                boolean isHandled = CharityPlugin.getStorage(KEY_DONATION, "" + donation.getId());
                if (!isHandled) {
//Logger.getGlobal().info("not Handled!");
                    int rewardId = donation.getRewardId();
                    Reward reward = registeredRewards.get(rewardId);
//Logger.getGlobal().info("Update Donation: " + donation.getName()+" "+donation.getReward()+" "+(donation.getReward()!=null?donation.getReward().getAction():"nullll"));
                    if (reward != null) {
//Logger.getGlobal().info("has Reward!");
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

    public void setCooldown(int maxDonationCooldown) {
        this.maxDonationCooldown = maxDonationCooldown;
        if(donationCooldown > this.maxDonationCooldown) donationCooldown = this.maxDonationCooldown;
    }

    public void setCooldown(String script, int maxDonationCooldown) {
        this.scriptCooldowns.put(script, new RewardCooldown(maxDonationCooldown));
    }

    private static class RewardCooldown {
        private int currentCooldown = 0;
        private int maxCooldown;

        public RewardCooldown(int maxCooldown) {
            this.maxCooldown = maxCooldown;
        }

        public int getCurrentCooldown() {
            return currentCooldown;
        }

        public void setCurrentCooldown(int currentCooldown) {
            this.currentCooldown = currentCooldown;
        }

        public int getMaxCooldown() {
            return maxCooldown;
        }

        public void setMaxCooldown(int maxCooldown) {
            this.maxCooldown = maxCooldown;
            if (currentCooldown > maxCooldown) {
                currentCooldown = maxCooldown;
            }
        }

        public boolean isActive() {
            return currentCooldown > 0;
        }

        public void decrement() {
            if (currentCooldown > 0) {
                currentCooldown--;
            }
        }

        public void reset() {
            currentCooldown = maxCooldown;
        }
    }
}
