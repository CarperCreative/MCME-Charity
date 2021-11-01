package com.mcmiddleearth.mcmecharity.incentives;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Poll {

    @SerializedName("id") int id;
    @SerializedName("campaignId") int champaignId;
    @SerializedName("name") String name;
    @SerializedName("active") boolean active;
    @SerializedName("options") PollOption[] options;

    private long handled;
    private long period = 300000; //5 minutes

    public PollOption[] getOptions() {
        return options;
    }

    public long getHandled() {
        return handled;
    }

    public void setHandled(long handled) {
        this.handled = handled;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public int getId() {
        return id;
    }

    public int getChampaignId() {
        return champaignId;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
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
