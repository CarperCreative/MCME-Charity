package com.mcmiddleearth.mcmecharity.actions;

import com.mcmiddleearth.mcmescripts.MCMEScripts;

import java.util.Arrays;
import java.util.List;

public class ScriptAction implements Action {

    private final String script, name;
    private final String[] arguments;

    public ScriptAction(String script, String name, String[] arguments) {
        this.script = script;
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public void execute(String donor, String message, String amount) {
        List<String> list = Arrays.asList(arguments);
        if(donor != null) list.add("name:"+donor);
        if(message!=null) list.add("message:"+message);
        if(amount!=null) list.add("amount:"+amount);
        MCMEScripts.getExternalTriggerManager().call(script,name,list.toArray(new String[0]));
    }
}
