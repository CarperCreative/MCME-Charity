package com.mcmiddleearth.mcmecharity.actions;

import com.mcmiddleearth.mcmecharity.CharityPlugin;
import com.mcmiddleearth.mcmescripts.MCMEScripts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class ScriptAction implements Action {

    private final String script, name;
    private final String[] arguments;

    public ScriptAction(String script, String name, String[] arguments) {
        this.script = script;
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public String getCooldownGroupName() {
        return "script." + script;
    }

    @Override
    public void execute(String donor, String message, String amount) {
        if(CharityPlugin.getStreamer()!=null) {
            List<String> list = new ArrayList<>(Arrays.asList(arguments));
            list.add("player:" + CharityPlugin.getStreamer());
            if (donor != null) list.add("name:" + donor);
            if (message != null) list.add("message:" + message);
            if (amount != null) list.add("amount:" + amount);
//Logger.getGlobal().info("" + MCMEScripts.getExternalTriggerManager() + " " + script + " " + name + " " + list);
            MCMEScripts.getExternalTriggerManager().call(script, name, list.toArray(new String[0]));
        }
    }

    public String getScript() {
        return this.script;
    }
}
