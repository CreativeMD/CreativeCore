package team.creative.creativecore.common.config.premade;

import net.minecraft.server.level.ServerPlayer;
import team.creative.creativecore.CreativeCore;

public class Permission<T> extends NamedList<T> {
    
    private T value;
    
    public Permission(T defaultValue) {
        this.value = defaultValue;
        put("default", value);
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
    
    public T get(ServerPlayer player) {
        for (java.util.Map.Entry<String, T> pair : entrySet())
            if (!pair.getKey().equals("default") && CreativeCore.CONFIG.is(player, pair.getKey()))
                return pair.getValue();
        return value;
    }
    
}
