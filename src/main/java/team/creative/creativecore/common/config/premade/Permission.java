package team.creative.creativecore.common.config.premade;

import net.minecraft.world.entity.player.Player;
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
        put(usergroup, value);
        return this;
    }
    
    @Override
    public T put(String key, T value) {
        if (key.equals("default"))
            this.value = value;
        return super.put(key, value);
    }
    
    public T get(Player player) {
        for (java.util.Map.Entry<String, T> pair : entrySet())
            if (!pair.getKey().equals("default") && CreativeCore.CONFIG.is(player, pair.getKey()))
                return pair.getValue();
        return value;
    }
    
    @Deprecated
    public T getDirect(Object key) {
        return super.get(key);
    }
    
    @Override
    @Deprecated
    public T get(Object key) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    @Deprecated
    public T getOrDefault(Object key, T defaultValue) {
        throw new UnsupportedOperationException();
    }
    
}
