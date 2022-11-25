package com.mcmiddleearth.mcmecharity.actions;

public interface Action {
    // I hate that this isn't part of the config -- opl
    /**
     * Returns the name of the group of rewards under which rewards using this action will all be rate-limited.
     *
     * @return {@code null} if rewards using this action should use the global cooldown, name of cooldown otherwise.
     */
    String getCooldownGroupName();

    void execute(String donor, String message, String amount);
}
