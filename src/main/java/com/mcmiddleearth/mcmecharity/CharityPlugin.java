package com.mcmiddleearth.mcmecharity;

import com.mcmiddleearth.mcmecharity.tiltify.TiltifyConnector;
import com.mcmiddleearth.mcmecharity.tiltify.TiltifyUpdater;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class CharityPlugin extends JavaPlugin implements CommandExecutor {

    private TiltifyConnector tiltifyConnector;
    private RewardManager rewardManager;

    private BukkitTask minecraftUpdater;
    private BukkitTask tiltifyUpdater;

    @Override
    public void onEnable() {
        tiltifyConnector = new TiltifyConnector();
        rewardManager = new RewardManager();
        Objects.requireNonNull(Bukkit.getServer().getPluginCommand("charity")).setExecutor(this);
        minecraftUpdater = new MinecraftUpdater(tiltifyConnector, rewardManager).runTaskTimer(this,410,100);
        tiltifyUpdater = new TiltifyUpdater(tiltifyConnector).runTaskTimerAsynchronously(this,400,400);
    }

    @Override
    public void onDisable() {
        minecraftUpdater.cancel();
        tiltifyUpdater.cancel();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
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
        }
        return true;
    }
}
