package com.creativemd.creativecore.common.config.premade;

import com.creativemd.creativecore.CreativeCore;

import net.minecraft.entity.player.EntityPlayer;

public class Permission<T> extends NamedList<T> {
    
    private T value;
    
    public Permission(T defaultValue) {
        this.value = defaultValue;
    }
    
    public T getDefault() {
        return value;
    }
    
    public Permission<T> add(String usergroup, T value) {
        if (usergroup.equals("default"))
            this.value = value;
        else
            super.put(usergroup, value);
        return this;
    }
    
    public T get(EntityPlayer player) {
        for (java.util.Map.Entry<String, T> pair : entrySet())
            if (CreativeCore.config.is(player, pair.getKey()))
                return pair.getValue();
        return value;
    }
    
}
