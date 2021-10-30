package com.mcmiddleearth.mcmecharity.incentives;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Challenge extends Incentive {

    @SerializedName("id") int id;
    @SerializedName("campaignId") int champaignId;
    @SerializedName("name") String name;
    @SerializedName("amount") double amount;
    @SerializedName("totalAmountRaised") double totalAmountRaised;
    @SerializedName("active") boolean active;
    @SerializedName("endsAt") long endsAt;

    private boolean handled;

    public int getId() {
        return id;
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    public long getEndsAt() {
        return endsAt;
    }

    public int getChampaignId() {
        return champaignId;
    }

    public double getAmount() {
        return amount;
    }

    public double getTotalAmountRaised() {
        return totalAmountRaised;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Challenge challenge = (Challenge) o;
        return id == challenge.id &&
                champaignId == challenge.champaignId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, champaignId);
    }
}
