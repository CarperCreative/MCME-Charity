package com.mcmiddleearth.mcmecharity.actions;

import com.mcmiddleearth.mcmescripts.MCMEScripts;

public class BossBattleAction implements Action {

    private final String bossBattleName;

    public BossBattleAction(String bossBattleName) {
        this.bossBattleName = bossBattleName;
    }

    @Override
    public void execute(String donor, String message, String amount) {
        MCMEScripts.getBossBattleManager().startBossBattle(bossBattleName,donor,message,amount);
    }
}