package com.mcmiddleearth.mcmecharity;

import com.mcmiddleearth.mcmecharity.managers.ChallengeManager;
import com.mcmiddleearth.mcmecharity.managers.PollManager;
import com.mcmiddleearth.mcmecharity.managers.RewardManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class CharityPlugin extends JavaPlugin implements CommandExecutor {

    private BukkitTask minecraftUpdater;
    private BukkitTask tiltifyUpdater;

    private static CharityPlugin instance;

    private FileConfiguration storage;
    private static String storageFilename = "storage.yml";

    @Override
    public void onEnable() {
        //tiltifyConnector = new TiltifyConnector();
        //private TiltifyConnector tiltifyConnector;
        saveDefaultConfig();
        try {
            storage.load(new File(getDataFolder(),storageFilename));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        RewardManager rewardManager = new RewardManager();
        PollManager pollManager = new PollManager();
        ChallengeManager challengeManager = new ChallengeManager();

        Objects.requireNonNull(Bukkit.getServer().getPluginCommand("charity")).setExecutor(this);

        minecraftUpdater = new MinecraftUpdater(rewardManager, pollManager, challengeManager).runTaskTimer(this,410,100);
        tiltifyUpdater = new TiltifyUpdater(rewardManager, pollManager, challengeManager).runTaskTimerAsynchronously(this,400,400);

        instance = this;
    }

    @Override
    public void onDisable() {
        minecraftUpdater.cancel();
        tiltifyUpdater.cancel();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        /*if(sender instanceof Player) {
            if (args.length > 0 && args[0].equalsIgnoreCase("on")) {
                rewardManager.addPlayer((Player) sender);
                sender.sendMessage("You'll get charity messages now.");
            } else if (args.length > 0 && args[0].equalsIgnoreCase("off")) {
                rewardManager.removePlayer((Player) sender);
                sender.sendMessage("You'll no longer get any charity messages.");
            } else {
                sender.sendMessage("Invalid or missing argument.");
            }
        } else {
            sender.sendMessage("Player only command.");
        }*/
        return true;
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

}
