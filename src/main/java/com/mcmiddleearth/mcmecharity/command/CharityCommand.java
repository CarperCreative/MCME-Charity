package com.mcmiddleearth.mcmecharity.command;

import com.mcmiddleearth.mcmecharity.CharityPlugin;
import com.mcmiddleearth.mcmecharity.TiltifyUpdater;
import com.mcmiddleearth.mcmecharity.actions.InventoryReplaceAction;
import com.mcmiddleearth.mcmecharity.managers.RewardManager;
import com.mcmiddleearth.mcmecharity.managers.RewardManager.RewardCooldown;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

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
                RewardManager rewardManager = CharityPlugin.getInstance().getRewardManager();

                if (args.length == 2) {
                    if (args[1].equalsIgnoreCase("list")) {
                        Set<String> knownCooldownGroupNames = rewardManager.getKnownCooldownGroupNames();
                        if (knownCooldownGroupNames.isEmpty()) {
                            sender.sendMessage(ChatColor.AQUA + "[CharityPlugin] Still loading...");
                        } else {
                            String list = knownCooldownGroupNames.stream()
                                    .sorted()
                                    .collect(Collectors.joining(", "));
                            sender.sendMessage(ChatColor.AQUA + "[CharityPlugin] Known group names: " + list);
                        }
                        return true;
                    }
                } else {
                    if (args[1].equalsIgnoreCase("get")) {
                        for (int index = 2; index < args.length; index++) {
                            String groupName = args[index];
                            RewardCooldown cooldown = rewardManager.getCooldownOrNull(groupName);
                            sender.sendMessage(ChatColor.AQUA+"[CharityPlugin] " + stringifyCooldown(groupName, cooldown));
                        }
                        return true;
                    } else if (args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("setmax")) {
                        if (args.length < 4) {
                            sender.sendMessage(ChatColor.AQUA+"[CharityPlugin] Usage: /charity cooldown " + args[1] + " <new value> <group name>...");
                            return true;
                        } else {
                            BiConsumer<RewardCooldown, Integer> setter = args[1].equalsIgnoreCase("set") ? RewardCooldown::setCurrentCooldown : RewardCooldown::setMaxCooldown;
                            try {
                                int newCooldown = Integer.parseInt(args[2]);

                                for (int index = 3; index < args.length; index++) {
                                    String groupName = args[index];
                                    RewardCooldown cooldown = rewardManager.getOrCreateCooldown(groupName);
                                    setter.accept(cooldown, newCooldown);
                                    sender.sendMessage(ChatColor.AQUA+"[CharityPlugin] " + stringifyCooldown(groupName, cooldown));
                                }
                            } catch (NumberFormatException ex) {
                                sender.sendMessage(ChatColor.RED+"[CharityPlugin] Invalid number given for cooldown.");
                            }
                            return true;
                        }
                    }
                }
            } else if(args.length>1 && args[0].equalsIgnoreCase("restoreinv")) {
                int back = 0;
                if(args.length>2) {
                    try {
                        back = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ex) {
                        sender.sendMessage(ChatColor.RED + "[CharityPlugin] Invalid numeric format for inventory restore. Restoring latest save.");
                    }
                }
                InventoryReplaceAction.loadInventory(back);
                sender.sendMessage(ChatColor.AQUA+"[CharityPlugin] Restoring inventory!");
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

    private String stringifyCooldown(String groupName, RewardCooldown cooldown) {
        if (cooldown == null) {
            return groupName + " does not exist";
        }

        return groupName + ": current = " + cooldown.getCurrentCooldown() + ", max = " + cooldown.getMaxCooldown();
    }

}
