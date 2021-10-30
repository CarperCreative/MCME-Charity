package com.mcmiddleearth.mcmecharity.managers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mcmiddleearth.mcmecharity.CharityPlugin;
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

    public static final String KEY_POLL = "poll";

    public PollManager() {
        polls = new HashSet<>();
    }

    public synchronized void handlePolls() {
        polls.stream().filter(poll -> poll.getHandled()+poll.getPeriod() < System.currentTimeMillis())
              .forEach(poll -> {
                  poll.setHandled(System.currentTimeMillis());
                  CharityPlugin.setStorageLong(KEY_POLL,poll.getChampaignId()+"_"+poll.getId(),System.currentTimeMillis(), false);
                  PollOption top = null;
                  for(int i = 0; i < poll.getOptions().length; i++) {
                      if(top == null || top.getTotalAmountRaised() < poll.getOptions()[i].getTotalAmountRaised()) {
                          top = poll.getOptions()[i];
                      }
                  }
                  if(top != null && top.getAction() != null) {
                      top.getAction().execute();
                  }
              });
        CharityPlugin.saveStorage();
    }

    public synchronized void updatePolls(String pollData) {
        polls.clear();
        Gson gson = new Gson();
        JsonElement pollDataJson =  JsonParser.parseString(pollData);
        JsonArray pollListJson = pollDataJson.getAsJsonObject().get("data").getAsJsonArray();
        for(int i = 0; i< pollListJson.size(); i++) {
            Poll poll = gson.fromJson(pollListJson.get(i), Poll.class);
            long period = 300000;
            try {
                period = Long.parseLong(CharityPlugin.getConfigString(KEY_POLL+"."+poll.getId()));
            } catch(NumberFormatException ignore) {}
            long handled = CharityPlugin.getStorageLong(KEY_POLL,poll.getChampaignId()+"_"+poll.getId());
            if(handled == 0) {
                poll.setHandled(System.currentTimeMillis() + (long) (Math.random() * period));
            } else {
                poll.setHandled(handled);
            }
            poll.setPeriod(period);
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
