package com.mcmiddleearth.mcmecharity.managers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mcmiddleearth.mcmecharity.CharityPlugin;
import com.mcmiddleearth.mcmecharity.actions.ActionCompiler;
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

    private final JsonParser jsonParser = new JsonParser();

    public synchronized void handlePolls() {
        if(CharityPlugin.getStreamer()!=null) {
            polls.stream().filter(poll -> poll.isActive() && poll.getHandled() + poll.getPeriod() < System.currentTimeMillis())
                .forEach(poll -> {
                    poll.setHandled(System.currentTimeMillis());
                    CharityPlugin.setStorageLong(KEY_POLL, poll.getChampaignId() + "_" + poll.getId(), System.currentTimeMillis(), false);
                    PollOption top = null;
                    for (int i = 0; i < poll.getOptions().length; i++) {
                        if (top == null || top.getTotalAmountRaised() < poll.getOptions()[i].getTotalAmountRaised()) {
                            top = poll.getOptions()[i];
                        }
                    }
                    if (top != null && top.getAction() != null) {
                        StringBuilder message = new StringBuilder(poll.getName());
                        double amount = 0;
                        for (int i = 0; i < poll.getOptions().length; i++) {
                            message.append(" - ").append(poll.getOptions()[i].getName()).append(": ").append(poll.getOptions()[i].getTotalAmountRaised());
                            amount = amount + poll.getOptions()[i].getTotalAmountRaised();
                        }
                        top.getAction().execute(null, message.toString(), amount + "");
                    }
                });
            CharityPlugin.saveStorage();
        }
    }

    public synchronized void updatePolls(String pollData) {
        polls.clear();
        Gson gson = new Gson();
        JsonElement pollDataJson =  jsonParser.parse(pollData);
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
