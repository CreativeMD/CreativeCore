package team.creative.creativecore.common.config.converation;

import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.common.config.gui.IGuiConfigParent;
import team.creative.creativecore.common.config.gui.PermissionGuiLayer;
import team.creative.creativecore.common.config.holder.ConfigKey.ConfigKeyField;
import team.creative.creativecore.common.config.premade.Permission;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.sync.GuiSyncGlobalLayer;
import team.creative.creativecore.common.gui.sync.GuiSyncHolder;

public class ConfigTypePermission extends ConfigTypeNamedList<Permission> {
    
    public static final GuiSyncGlobalLayer<PermissionGuiLayer> PERMISSION_DIALOG = GuiSyncHolder.GLOBAL.layer("permission_dialog", (t) -> new PermissionGuiLayer());
    
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
    
    public boolean areEqual(Permission one, Permission two, Class clazz) {
        ConfigTypeConveration conversation = getUnsafe(clazz);
        
        if (one.size() != two.size())
            return false;
        
        if (conversation != null && !conversation.areEqual(one.getDefault(), two.getDefault(), null))
            return false;
        
        if (conversation == null && !one.getDefault().equals(two.getDefault()) && !EqualsBuilder.reflectionEquals(one.getDefault(), two.getDefault(), false))
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
    
    @Override
    public boolean areEqual(Permission one, Permission two, @Nullable ConfigKeyField key) {
        return areEqual(one, two, getListType(key));
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void createControls(GuiParent parent, IGuiConfigParent configParent, @Nullable ConfigKeyField key, Class clazz) {
        parent.add(new GuiPermissionConfigButton("button", this, getListType(key), configParent));
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void loadValue(Permission value, GuiParent parent, IGuiConfigParent configParent, @Nullable ConfigKeyField key) {
        GuiPermissionConfigButton button = parent.get("button");
        button.value = value;
        button.defaultValue = (Permission<?>) key.getDefault();
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected Permission saveValue(GuiParent parent, IGuiConfigParent configParent, Class clazz, @Nullable ConfigKeyField key) {
        return parent.get("button", GuiPermissionConfigButton.class).value;
    }
    
    public static class GuiPermissionConfigButton extends GuiButton {
        
        public Permission<?> value;
        public Permission<?> defaultValue;
        public Class clazz;
        public ConfigTypePermission configTypePerm;
        public IGuiConfigParent configParent;
        
        public GuiPermissionConfigButton(String name, ConfigTypePermission configTypePerm, Class clazz, IGuiConfigParent configParent) {
            super(name, null);
            this.clazz = clazz;
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
