package com.mcmiddleearth.mcmecharity;

import com.mcmiddleearth.mcmecharity.command.CharityCommand;
import com.mcmiddleearth.mcmecharity.listener.OneRingEffect;
import com.mcmiddleearth.mcmecharity.listener.PlayerListener;
import com.mcmiddleearth.mcmecharity.managers.ChallengeManager;
import com.mcmiddleearth.mcmecharity.managers.PollManager;
import com.mcmiddleearth.mcmecharity.managers.RewardManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

public final class CharityPlugin extends JavaPlugin {

    private BukkitTask minecraftUpdater;
    private BukkitTask tiltifyUpdater;

    private static CharityPlugin instance;

    private FileConfiguration storage;
    private static final String storageFilename = "storage.yml";

    RewardManager rewardManager;
    PollManager pollManager;
    ChallengeManager challengeManager;

    @Override
    public void onEnable() {
        //tiltifyConnector = new TiltifyConnector();
        //private TiltifyConnector tiltifyConnector;
        saveDefaultConfig();

        rewardManager = new RewardManager();
        pollManager = new PollManager();
        challengeManager = new ChallengeManager();

        Objects.requireNonNull(Bukkit.getServer().getPluginCommand("charity")).setExecutor(new CharityCommand());

        minecraftUpdater = new MinecraftUpdater(rewardManager, pollManager, challengeManager).runTaskTimer(this,210,100);
        tiltifyUpdater = new TiltifyUpdater(rewardManager, pollManager, challengeManager).runTaskTimerAsynchronously(this,200,100);

        instance = this;
        loadStorage();

        Bukkit.getPluginManager().registerEvents(new PlayerListener(),this);
        Bukkit.getPluginManager().registerEvents(new OneRingEffect(),this);
    }

    @Override
    public void onDisable() {
        minecraftUpdater.cancel();
        tiltifyUpdater.cancel();
    }

    public synchronized static String getConfigString(String key) {
        return instance.getConfig().getString(key);
    }

    public synchronized static boolean getStorage(String key, String id) {
        return instance.storage.getBoolean(key+"."+id,false);
    }

    public synchronized static void setStorage(String key, String id, boolean value, boolean saveToFile) {
        instance.storage.set(key + "." + id, value);
        if (saveToFile) {
            saveStorage();
        }
    }

    public synchronized static long getStorageLong(String key, String id) {
        return instance.storage.getLong(key+"."+id,0L);
    }

    public synchronized static void setStorageLong(String key, String id, long value, boolean saveToFile) {
        instance.storage.set(key + "." + id, value);
        if (saveToFile) {
            saveStorage();
        }
    }

    public synchronized static void saveStorage() {
        try {
            instance.storage.save(new File(instance.getDataFolder(),storageFilename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void loadStorage() {
        try {
            instance.storage = new YamlConfiguration();
            File storageFile = new File(instance.getDataFolder(),storageFilename);
            if(!storageFile.exists())
                if(storageFile.createNewFile())
                    instance.getLogger().severe("Can't create storage file!");
            instance.storage.load(storageFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static Player getStreamer() {
        String name = getConfigString("streamer");
        if(name==null) {
            return null;
        } else {
            return Bukkit.getPlayer(name);
        }
    }

    public static CharityPlugin getInstance() {
        return instance;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public PollManager getPollManager() {
        return pollManager;
    }

    public ChallengeManager getChallengeManager() {
        return challengeManager;
    }
}
