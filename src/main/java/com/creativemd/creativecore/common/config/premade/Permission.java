package com.creativemd.creativecore.common.config.premade;

import java.util.function.Supplier;

import com.creativemd.creativecore.CreativeCore;
import com.creativemd.creativecore.common.utils.type.Pair;
import com.creativemd.creativecore.common.utils.type.PairList;

import net.minecraft.entity.player.EntityPlayer;

public class Permission<T> {
    
    private T value;
    private PairList<String, T> valueGroups = new PairList<>();
    private Supplier<T> factory;
    
    public Permission(T defaultValue) {
        this.value = defaultValue;
    }
    
    public Permission(Supplier<T> factory) {
        this.value = factory.get();
        this.factory = factory;
    }
    
    public T get(EntityPlayer player) {
        for (Pair<String, T> pair : valueGroups)
            if (CreativeCore.config.is(player, pair.getKey()))
                return pair.getValue();
        return value;
    }
    
}
