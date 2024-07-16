package team.creative.creativecore.common.config.converation;

import java.util.Map.Entry;
import java.util.Set;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.gui.IGuiConfigParent;
import team.creative.creativecore.common.config.gui.PermissionGuiLayer;
import team.creative.creativecore.common.config.key.ConfigKey;
import team.creative.creativecore.common.config.premade.Permission;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.sync.GuiSyncGlobalLayer;
import team.creative.creativecore.common.gui.sync.GuiSyncHolder;

public class ConfigTypePermission extends ConfigTypeNamedList<Permission> {
    
    public static final GuiSyncGlobalLayer<PermissionGuiLayer> PERMISSION_DIALOG = GuiSyncHolder.GLOBAL.layer("permission_dialog", (t) -> new PermissionGuiLayer());
    
    @Override
    protected Permission create(ConfigKey key, Side side) {
        ConfigKey listKey = ConfigKey.ofGenericType(key, side); // Will create a new default object right away
        return new Permission(listKey.get());
    }
    
    @Override
    protected void addToList(Permission list, String name, Object object) {
        list.add(name, object);
    }
    
    @Override
    public boolean shouldSave(Permission value, GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
        return !areEqual(value, (Permission) key.get(), key, side);
    }
    
    @Override
    public boolean areEqual(Permission one, Permission two, ConfigKey key, Side side) {
        ConfigKey listKey = ConfigKey.ofGenericType(key, side);
        ConfigTypeConveration converation = listKey.converation();
        
        if (one.size() != two.size())
            return false;
        
        if (converation != null) {
            listKey.forceValue(one.getDefault(), side);
            if (!converation.areEqual(one.getDefault(), two.getDefault(), listKey, side))
                return false;
            
        } else if (converation == null && !one.getDefault().equals(two.getDefault()))
            return false;
        
        for (Entry<String, ?> entry : (Set<Entry<String, ?>>) one.entrySet()) {
            Object other = two.getDirect(entry.getKey());
            
            if (converation != null) {
                listKey.forceValue(entry.getValue(), side);
                if (!converation.areEqual(entry.getValue(), other, listKey, side))
                    return false;
                
            } else if (converation == null && !entry.getValue().equals(other))
                return false;
        }
        
        return true;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void createControls(GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
        parent.add(new GuiPermissionConfigButton("button", this, ConfigKey.ofGenericType(key, side), configParent, side));
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public void loadValue(Permission value, Permission defaultValue, GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
        GuiPermissionConfigButton button = parent.get("button");
        button.value = value;
        button.defaultValue = defaultValue;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected Permission saveValue(GuiParent parent, IGuiConfigParent configParent, ConfigKey key, Side side) {
        return parent.get("button", GuiPermissionConfigButton.class).value;
    }
    
    public static class GuiPermissionConfigButton extends GuiButton {
        
        public Permission<?> value;
        public Permission<?> defaultValue;
        public ConfigKey key;
        public ConfigTypePermission configTypePerm;
        public IGuiConfigParent configParent;
        public final Side side;
        
        public GuiPermissionConfigButton(String name, ConfigTypePermission configTypePerm, ConfigKey key, IGuiConfigParent configParent, Side side) {
            super(name, null);
            this.key = key;
            this.configTypePerm = configTypePerm;
            this.configParent = configParent;
            pressed = x -> {
                PermissionGuiLayer layer = PERMISSION_DIALOG.open(getIntegratedParent(), new CompoundTag());
                layer.button = this;
                layer.init();
            };
            this.side = side;
            setTranslate("gui.perm.open");
        }
        
        public void setNewValue(Permission permission) {
            this.value = permission;
            configParent.changed();
        }
        
    }
}
