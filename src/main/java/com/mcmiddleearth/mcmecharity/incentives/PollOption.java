package com.mcmiddleearth.mcmecharity.incentives;

import com.google.gson.annotations.SerializedName;

public class PollOption extends Incentive {

    @SerializedName("id")
    int id;
    @SerializedName("campaignId")
    int champaignId;
    @SerializedName("name")
    String name;
    @SerializedName("active")
    boolean active;
    @SerializedName("totalAmountRaised")
    double totalAmountRaised;

    public int getId() {
        return id;
    }
}
