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

    public int getId() {
        return id;
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
