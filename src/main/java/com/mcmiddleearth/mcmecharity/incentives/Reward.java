package com.mcmiddleearth.mcmecharity.incentives;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Reward extends Incentive {

    @SerializedName("id") int id;
    @SerializedName("campaignId") int champaignId;
    @SerializedName("name") String name;
    @SerializedName("description") String description;
    @SerializedName("kind") String kind;
    @SerializedName("active") boolean active;
    //@SerializedName("quantity") double quantity;
    //@SerializedName("endsAt") long endsAt;
    //@SerializedName("createdAt") long createdAt;
    //@SerializedName("updatedAt") long updatedAt;


    public int getId() {
        return id;
    }

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
