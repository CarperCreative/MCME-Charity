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
import com.mcmiddleearth.mcmecharity.incentives.Reward;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class RewardManager {
    public static final String COOLDOWN_GLOBAL = "_global";
    public static final String COOLDOWN_UNGROUPED = "_ungrouped";

    //private final Set<Player> registeredPlayers = new HashSet<>();

    private final Map<Integer, Reward> registeredRewards = new HashMap<>();

    private final List<Donation> donations = new ArrayList<>();

    private static final String KEY_REWARD = "reward",
                                KEY_DONATION = "donation";

    private final JsonParser jsonParser = new JsonParser();

    /**
     * Cooldown which applies to every single reward.
     */
    private final RewardCooldown globalCooldown = new RewardCooldown(COOLDOWN_GLOBAL, 0);

    /**
     * Mapping of all existing cooldown groups, including the special global and ungrouped.
     */
    private final Map<String, RewardCooldown> cooldownGroups = new HashMap<>();

    /**
     * Cooldown which applies to all reward actions which don't specify a cooldown group name (return {@code null}).
     */
    private final RewardCooldown ungroupedCooldown = new RewardCooldown(COOLDOWN_UNGROUPED, 20);

    private final Set<String> knownCooldownGroupNames = new HashSet<>();

    public RewardManager() {
        cooldownGroups.put(COOLDOWN_GLOBAL, globalCooldown);
        cooldownGroups.put(COOLDOWN_UNGROUPED, ungroupedCooldown);
    }

    public synchronized void updateRewards(String rewardData) {
        registeredRewards.clear();

        knownCooldownGroupNames.clear();
        knownCooldownGroupNames.add(COOLDOWN_GLOBAL);
        knownCooldownGroupNames.add(COOLDOWN_UNGROUPED);

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

                String cooldownGroupName = action.getCooldownGroupName();
                if (cooldownGroupName != null) {
                    knownCooldownGroupNames.add(cooldownGroupName);
                }
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

            if (globalCooldown.isActive()) {
                continue;
            }

            final Reward reward = donation.getReward();
            final Action action = reward.getAction();

            RewardCooldown cooldown = getCooldownForDonationOrNull(donation);
            if (cooldown != null && cooldown.isActive()) {
                continue;
            }

            globalCooldown.reset();
            if (cooldown != null) {
                cooldown.reset();
            }

            this.giveReward(donation);

            break;
        }

        CharityPlugin.saveStorage();

        for (final RewardCooldown cooldown : this.cooldownGroups.values()) {
            cooldown.decrement();
        }

        Logger.getLogger(this.getClass().getSimpleName()).info("Donation queue size: " + donations.size() + " - Global cooldown: " + globalCooldown.getCurrentCooldown());
    }

    private void giveReward(Donation donation) {
        String cooldownGroupName = getCooldownGroupNameForDonationOrNull(donation);
        RewardCooldown cooldown = getCooldownOrNull(cooldownGroupName);
        Logger.getLogger(RewardManager.class.getSimpleName()).info("Giving donation reward " + donation.getReward().getDescription() + " for " + donation.getName() + "." + (cooldown == null ? "" : " New cooldown for group " + cooldownGroupName + ": " + cooldown.getCurrentCooldown()));
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

    public RewardCooldown getCooldownOrNull(String groupName) {
        if (groupName == null) return null;
        return cooldownGroups.get(groupName);
    }

    public RewardCooldown getOrCreateCooldown(String groupName) {
        return this.cooldownGroups.computeIfAbsent(groupName, key -> new RewardCooldown(key, 0));
    }

    private RewardCooldown getCooldownForDonationOrNull(Donation donation) {
        String cooldownGroupName = getCooldownGroupNameForDonationOrNull(donation);
        return getCooldownOrNull(cooldownGroupName);
    }

    private String getCooldownGroupNameForDonationOrNull(Donation donation) {
        final Reward reward = donation.getReward();
        if (reward == null) return null;
        final Action action = reward.getAction();
        if (action == null) return null;
        final String cooldownGroupName = action.getCooldownGroupName();
        return cooldownGroupName == null ? COOLDOWN_UNGROUPED : cooldownGroupName;
    }

    public Map<String, RewardCooldown> getCooldownGroups() {
        return cooldownGroups;
    }

    public Set<String> getKnownCooldownGroupNames() {
        return knownCooldownGroupNames;
    }

    public void setCooldown(int maxDonationCooldown) {
        globalCooldown.setMaxCooldown(maxDonationCooldown);
    }

    public static class RewardCooldown {
        private String groupName;
        private int currentCooldown = 0;
        private int maxCooldown;

        public RewardCooldown(String groupName, int maxCooldown) {
            this.groupName = groupName;
            this.maxCooldown = maxCooldown;
        }

        public String getGroupName() {
            return groupName;
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
