package com.mcmiddleearth.mcmecharity.tiltify.incentives;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Reward implements Incentive {

    @SerializedName("id") int id;
    @SerializedName("type") String type;
    @SerializedName("name") String name;
    @SerializedName("totalAmountRaised") double totalAmount;
    @SerializedName("amount") double amount;
    @SerializedName("campaignId") int champaignId;
    @SerializedName("active") boolean active;
    @SerializedName("endsAt") long endsAt;
    @SerializedName("createdAt") long createdAt;
    @SerializedName("updatedAt") long updatedAt;

    @Override
    public String getDescription() {
        return name+" "+id+" "+champaignId;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Reward && this.id == ((Reward) other).id && this.champaignId == ((Reward) other).champaignId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, champaignId);
    }
}
