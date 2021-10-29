package com.mcmiddleearth.mcmecharity.managers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mcmiddleearth.mcmecharity.Donation;
import com.mcmiddleearth.mcmecharity.actions.Action;
import com.mcmiddleearth.mcmecharity.actions.ActionCompiler;
import com.mcmiddleearth.mcmecharity.incentives.Challenge;
import com.mcmiddleearth.mcmecharity.incentives.Poll;
import com.mcmiddleearth.mcmecharity.incentives.PollOption;

import java.util.HashSet;
import java.util.Set;

public class PollManager {

    private final Set<Poll> polls;

    private static final String KEY_POLL = "poll";

    public PollManager() {
        polls = new HashSet<>();
    }

    public void handlePolls() {
    }

    public void updatePolls(String pollData) {
        polls.clear();
        Gson gson = new Gson();
        JsonElement pollDataJson =  JsonParser.parseString(pollData);
        JsonArray pollListJson = pollDataJson.getAsJsonObject().get("data").getAsJsonArray();
        for(int i = 0; i< pollListJson.size(); i++) {
            Poll poll = gson.fromJson(pollListJson.get(i), Poll.class);
            PollOption[] options = poll.getOptions();
            boolean foundOne = false;
            for(PollOption option: options) {
                option.setAction(ActionCompiler.compile(KEY_POLL, option.getId()));
                foundOne = true;
            }
            if(foundOne) {
                polls.add(poll);
            }
        }
    }
}
