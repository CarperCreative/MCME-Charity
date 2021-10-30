package com.mcmiddleearth.mcmecharity;

import com.google.gson.annotations.SerializedName;
import com.mcmiddleearth.mcmecharity.incentives.Reward;

import javax.annotation.Nullable;
import java.util.Objects;

public class Donation {

    @SerializedName("id") int id;
    @SerializedName("name") String name;
    @SerializedName("comment") String comment;
    @SerializedName("amount") double amount;
    @SerializedName("rewardId") int rewardId;

    private @Nullable Reward reward;

    private boolean handled = false;

    public int getRewardId() {
        return rewardId;
    }

    @Nullable
    public Reward getReward() {
        return reward;
    }

    public void setReward(@Nullable Reward reward) {
        this.reward = reward;
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Donation donation = (Donation) o;
        return id == donation.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
