package com.mcmiddleearth.mcmecharity.actions;

import com.mcmiddleearth.mcmecharity.CharityPlugin;

import java.util.Arrays;

public class ActionCompiler {

    public static Action compile(String key, int id) {
        Action result = null;
        String actionString = CharityPlugin.getConfigString(key+"."+id);
        if(actionString!=null) {
            String[] actionData = actionString.split(" ");
            switch (actionData[0]) {
                case "script":
                    if(actionData.length > 2) {
                        result = new ScriptAction(actionData[1], actionData[2], Arrays.copyOfRange(actionData, 3, actionData.length));
                    }
                    break;
                case "entity":
                    result = new EntitiesAction(Arrays.copyOfRange(actionData, 1, actionData.length));
                    break;
            }
        }
        return result;
    }
}
