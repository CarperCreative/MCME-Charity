package com.mcmiddleearth.mcmecharity.incentives;

import com.mcmiddleearth.mcmecharity.actions.Action;

public abstract class Incentive {

    private Action action;

    public void setAction(Action action) {
        this.action = action;
    }
    public Action getAction() {
        return action;
    }

}
