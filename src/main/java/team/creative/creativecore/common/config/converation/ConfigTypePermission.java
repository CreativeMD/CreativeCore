package team.creative.creativecore.common.config.converation;

import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.common.config.gui.IGuiConfigParent;
import team.creative.creativecore.common.config.gui.PermissionGuiLayer;
import team.creative.creativecore.common.config.key.ConfigKey;
import team.creative.creativecore.common.config.key.ConfigKeyCache;
import team.creative.creativecore.common.config.key.ConfigKeyCacheType;
import team.creative.creativecore.common.config.premade.Permission;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.sync.GuiSyncGlobalLayer;
import team.creative.creativecore.common.gui.sync.GuiSyncHolder;

public class ConfigTypePermission extends ConfigTypeNamedList<Permission> {
    
    public static final GuiSyncGlobalLayer<PermissionGuiLayer> PERMISSION_DIALOG = GuiSyncHolder.GLOBAL.layer("permission_dialog", (p, t) -> new PermissionGuiLayer());
    
    @Override
    protected Permission create(ConfigKey key) {
        return new Permission(ConfigTypeConveration.createObject(key));
    }
    
    @Override
    protected void addToList(Permission list, String name, Object object) {
        list.add(name, object);
    }
    
    @Override
    public boolean shouldSave(Permission value, GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
        return !areEqual(value, (Permission) key.get(), key);
    }
    
    public boolean areEqual(Permission one, Permission two, ConfigTypeConveration converation) {
        if (one.size() != two.size())
            return false;
        
        if (converation != null && !converation.areEqual(one.getDefault(), two.getDefault(), null))
            return false;
        
        if (converation == null && !one.getDefault().equals(two.getDefault()) && !EqualsBuilder.reflectionEquals(one.getDefault(), two.getDefault(), false))
            return false;
        
        for (Entry<String, ?> entry : (Set<Entry<String, ?>>) one.entrySet()) {
            Object other = two.getDirect(entry.getKey());
            
            if (converation != null && !converation.areEqual(entry.getValue(), other, null))
                return false;
            
            if (converation == null && !entry.getValue().equals(other) && !EqualsBuilder.reflectionEquals(entry.getValue(), other, false))
                return false;
        }
        
        return true;
    }
    
    @Override
    public boolean areEqual(Permission one, Permission two, ConfigKey key) {
        ConfigKeyCache listKey = ConfigKeyCache.ofGenericType(key);
        return areEqual(one, two, listKey instanceof ConfigKeyCacheType t ? t.converation : null);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void createControls(GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
        parent.add(new GuiPermissionConfigButton("button", this, ConfigKeyCache.ofGenericType(key), configParent));
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void loadValue(Permission value, Permission defaultValue, GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
        GuiPermissionConfigButton button = parent.get("button");
        button.value = value;
        button.defaultValue = defaultValue;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected Permission saveValue(GuiParent parent, IGuiConfigParent configParent, ConfigKey key) {
        return parent.get("button", GuiPermissionConfigButton.class).value;
    }
    
    public static class GuiPermissionConfigButton extends GuiButton {
        
        public Permission<?> value;
        public Permission<?> defaultValue;
        public ConfigKeyCache key;
        public ConfigTypePermission configTypePerm;
        public IGuiConfigParent configParent;
        
        public GuiPermissionConfigButton(String name, ConfigTypePermission configTypePerm, ConfigKeyCache key, IGuiConfigParent configParent) {
            super(name, null);
            this.key = key;
            this.configTypePerm = configTypePerm;
            this.configParent = configParent;
            pressed = x -> {
                PermissionGuiLayer layer = PERMISSION_DIALOG.open(getIntegratedParent(), new CompoundTag());
                layer.button = this;
                layer.init();
            };
            setTranslate("gui.perm.open");
        }
        
        public void setNewValue(Permission permission) {
            this.value = permission;
            configParent.changed();
        }
        
    }
}
