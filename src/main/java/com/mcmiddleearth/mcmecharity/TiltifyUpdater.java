package com.mcmiddleearth.mcmecharity;

import com.mcmiddleearth.mcmecharity.managers.ChallengeManager;
import com.mcmiddleearth.mcmecharity.managers.PollManager;
import com.mcmiddleearth.mcmecharity.managers.RewardManager;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TiltifyUpdater extends BukkitRunnable {

    //private final TiltifyConnector connector;
    private final RewardManager rewardManager;
    private final PollManager pollManager;
    private final ChallengeManager challengeManager;

    private static final String apiUrl = "https://tiltify.com/api/v3/";
    private static final String
            KEY_CAMPAIGN_ID = "tiltify.campaign_id",
            KEY_BEARER      = "tiltify.bearer";

    public TiltifyUpdater(/*TiltifyConnector connector,*/ RewardManager rewardManager, PollManager pollManager,
                          ChallengeManager challengeManager) {
        //this.connector = connector;
        this.rewardManager = rewardManager;
        this.pollManager = pollManager;
        this.challengeManager = challengeManager;
    }

    @Override
    public void run() {
        try{
            /*String response = "";
            HttpClientBuilder clientBuilder = HttpClientBuilder.create().disableCookieManagement();
            try (CloseableHttpClient client = clientBuilder.build()) {

                HttpGet request = new HttpGet("https://tiltify.com/api/v3/campaigns/103853/rewards");

                request.addHeader("Authorization", "Bearer xxx");
                response = client.execute(request, httpResponse -> {
                    StringBuilder builder = new StringBuilder();
                    try(BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()))) {
                        String line;
                        while ((line = reader.readLine()) != null)
                        {
                            builder.append(line).append("\n");
                        }

                    }
                    return builder.toString();
                });
                //System.out.println(response);
            }*/
        /*try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL("https://tiltify.com/api/v3/campaigns/103853/rewards").openConnection();
            connection.setRequestProperty("Authorization", "Bearer xxx");
            connection.getRequestProperties().forEach((key,value) -> Logger.getGlobal().info(key+" "+value));
            connection.setConnectTimeout(10000); //10 seconds
            connection.connect();
            StringBuilder stringBuilder = new StringBuilder();
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null)
                {
                    stringBuilder.append(line).append("\n");
                }

            }
            connection.disconnect();*/
            //connector.handleRewardUpdate(response);
            rewardManager.updateRewards(fetch("rewards"));
            rewardManager.updateDonations(fetch("donations"));
            pollManager.updatePolls(fetch("polls"));
            challengeManager.updateChallenges(fetch("challenges"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String fetch(String key) throws IOException {
        String response = "";
        HttpClientBuilder clientBuilder = HttpClientBuilder.create().disableCookieManagement();
        try (CloseableHttpClient client = clientBuilder.build()) {

            HttpGet request = new HttpGet(apiUrl+"/campaigns/"+CharityPlugin.getConfigString(KEY_CAMPAIGN_ID)+"/"+key);

            request.addHeader("Authorization", "Bearer "+CharityPlugin.getConfigString(KEY_BEARER));
            response = client.execute(request, httpResponse -> {
                StringBuilder builder = new StringBuilder();
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()))) {
                    String line;
                    while ((line = reader.readLine()) != null)
                    {
                        builder.append(line).append("\n");
                    }

                }
                return builder.toString();
            });
            //System.out.println(response);
        }
        return response;
    }
}
