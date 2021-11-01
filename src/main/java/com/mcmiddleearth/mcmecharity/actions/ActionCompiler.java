package com.mcmiddleearth.mcmecharity.actions;

import com.mcmiddleearth.mcmecharity.CharityPlugin;
import org.bukkit.Material;

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
                case "bed":
                    result = new BedAction();
                    break;
                case "item_rain":
                    if(actionData.length > 1) {
                        try {
                            result = new ItemRainAction(Material.valueOf(actionData[1].toLowerCase()));
                        } catch (IllegalArgumentException ignore) {
                        }
                    }
                    break;
                case "replace_inventory":
                    if(actionData.length > 1) {
                        try {
                            result = new InventoryReplaceAction(Material.valueOf(actionData[1].toLowerCase()));
                        } catch (IllegalArgumentException ignore) {
                        }
                    }
                    break;
            }
        }
        return result;
    }
}
