package com.mcmiddleearth.mcmecharity.incentives;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Poll {

    @SerializedName("id") int id;
    @SerializedName("campaignId") int champaignId;
    @SerializedName("name") String name;
    @SerializedName("active") boolean active;
    @SerializedName("options") PollOption[] options;

    public PollOption[] getOptions() {
        return options;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Poll poll = (Poll) o;
        return id == poll.id &&
                champaignId == poll.champaignId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, champaignId);
    }

}
