package team.creative.creativecore.common.config.converation;

import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.jetbrains.annotations.Nullable;

import team.creative.creativecore.common.config.gui.IGuiConfigParent;
import team.creative.creativecore.common.config.holder.ConfigKey.ConfigKeyField;
import team.creative.creativecore.common.config.premade.Permission;
import team.creative.creativecore.common.gui.GuiParent;

public class ConfigTypePermission extends ConfigTypeNamedList<Permission> {
    
    @Override
    protected Permission create(Class clazz) {
        return new Permission(ConfigTypeConveration.createObject(clazz));
    }
    
    @Override
    protected void addToList(Permission list, String name, Object object) {
        list.add(name, object);
    }
    
    @Override
    public boolean shouldSave(Permission value, GuiParent parent, IGuiConfigParent configParent, ConfigKeyField key) {
        return !areEqual(value, (Permission) key.get(), key);
    }
    
    @Override
    public boolean areEqual(Permission one, Permission two, @Nullable ConfigKeyField key) {
        Class clazz = getListType(key);
        ConfigTypeConveration conversation = getUnsafe(clazz);
        
        if (one.size() != two.size())
            return false;
        
        for (Entry<String, ?> entry : (Set<Entry<String, ?>>) one.entrySet()) {
            Object other = two.getDirect(entry.getKey());
            
            if (conversation != null && !conversation.areEqual(entry.getValue(), other, null))
                return false;
            
            if (conversation == null && !entry.getValue().equals(other) && !EqualsBuilder.reflectionEquals(entry.getValue(), other, false))
                return false;
        }
        
        return true;
    }
}
