package com.mcmiddleearth.mcmecharity.managers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mcmiddleearth.mcmecharity.CharityPlugin;
import com.mcmiddleearth.mcmecharity.actions.Action;
import com.mcmiddleearth.mcmecharity.actions.ActionCompiler;
import com.mcmiddleearth.mcmecharity.incentives.Challenge;

import java.util.HashSet;
import java.util.Set;

public class ChallengeManager {

    private final Set<Challenge> challenges;

    private static final String KEY_CHALLENGE = "challenge";

    public ChallengeManager() {
        challenges = new HashSet<>();
    }

    public synchronized void handleChallenges() {
        try {
            challenges.stream().filter(challenge -> !challenge.isHandled() && challenge.getTotalAmountRaised() >= challenge.getAmount())
                    .forEach(challenge -> {
                        if(challenge.getAction()!=null) {
                            challenge.getAction().execute();
                        }
                        challenge.setHandled(true);
                        CharityPlugin.setStorage(KEY_CHALLENGE, challenge.getChampaignId()+"_"+challenge.getId(), true, false);
                    });
        } finally {
            CharityPlugin.saveStorage();
        }
    }


    public synchronized void updateChallenges(String challengeData) {
        challenges.clear();
        Gson gson = new Gson();
        JsonElement challengeDataJson =  JsonParser.parseString(challengeData);
        JsonArray challengeListJson = challengeDataJson.getAsJsonObject().get("data").getAsJsonArray();
        for(int i = 0; i< challengeListJson.size(); i++) {
            Challenge challenge = gson.fromJson(challengeListJson.get(i), Challenge.class);
            boolean isHandled = CharityPlugin.getStorage(KEY_CHALLENGE, challenge.getChampaignId()+"_"+challenge.getId());
            if(!isHandled && (challenge.getEndsAt() > System.currentTimeMillis()
                                || challenge.getTotalAmountRaised() >= challenge.getAmount())) {
                Action action = ActionCompiler.compile(KEY_CHALLENGE, challenge.getId());
                if (action != null) {
                    challenge.setAction(action);
                    challenges.add(challenge);
                }
            }
        }

    }
}
