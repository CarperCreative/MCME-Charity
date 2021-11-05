package com.mcmiddleearth.mcmecharity.command;

import com.mcmiddleearth.mcmecharity.CharityPlugin;
import com.mcmiddleearth.mcmecharity.TiltifyUpdater;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CharityCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender.isOp()) {
            if(args.length>0 && args[0].equalsIgnoreCase("test")) {
                TiltifyUpdater.setTesting(true);
                sender.sendMessage(ChatColor.AQUA+"[CharityPlugin] Using test data from plugin data folder!");
            } else if(args.length>0 && args[0].equalsIgnoreCase("live")) {
                TiltifyUpdater.setTesting(false);
                sender.sendMessage(ChatColor.AQUA+"[CharityPlugin] Using live data from Tiltify!");
            } else if(args.length>0 && args[0].equalsIgnoreCase("reload")) {
                CharityPlugin.getInstance().reloadConfig();
                sender.sendMessage(ChatColor.AQUA+"[CharityPlugin] Reloading configuration!");
            } else if(args.length>1 && args[0].equalsIgnoreCase("cooldown")) {
                try {
                    int cooldown = Integer.parseInt(args[1]);
                    CharityPlugin.getInstance().getRewardManager().setCooldown(cooldown);
                    sender.sendMessage(ChatColor.AQUA+"[CharityPlugin] Donation reward cooldown set to: "+cooldown);
                }catch (NumberFormatException ex) {
                    sender.sendMessage(ChatColor.RED+"[CharityPlugin] Invalid numeric format for donation cooldown.");
                }
            }
            /*else if (args.length > 0 && args[0].equalsIgnoreCase("on")) {
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
        }
        return true;
    }

}
