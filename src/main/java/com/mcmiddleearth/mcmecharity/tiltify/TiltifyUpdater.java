package com.mcmiddleearth.mcmecharity.tiltify;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.bukkit.scheduler.BukkitRunnable;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;

public class TiltifyUpdater extends BukkitRunnable {

    private final TiltifyConnector connector;

    public TiltifyUpdater(TiltifyConnector connector) {
        this.connector = connector;
    }
    @Override
    public void run() {
        try{
            String response = "";
            HttpClientBuilder clientBuilder = HttpClientBuilder.create().disableCookieManagement();
            try (CloseableHttpClient client = clientBuilder.build()) {

                HttpGet request = new HttpGet("https://tiltify.com/api/v3/campaigns/103853/rewards");

                request.addHeader("Authorization", "Bearer 3aa0b420fb02cfe5cca324576a238885279398b0bd19878de97b74edca6ad520");
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
        /*try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL("https://tiltify.com/api/v3/campaigns/103853/rewards").openConnection();
            connection.setRequestProperty("Authorization", "Bearer 3aa0b420fb02cfe5cca324576a238885279398b0bd19878de97b74edca6ad520");
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
            connector.handleRewardUpdate(response);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
