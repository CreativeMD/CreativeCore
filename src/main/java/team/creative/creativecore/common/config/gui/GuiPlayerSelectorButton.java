package team.creative.creativecore.common.config.gui;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.sync.GuiSyncGlobalLayer;
import team.creative.creativecore.common.gui.sync.GuiSyncHolder;
import team.creative.creativecore.common.util.player.PlayerSelector;

public class GuiPlayerSelectorButton extends GuiButton {
    
    public static final GuiSyncGlobalLayer<PlayerSelectorDialog> PLAYER_SELECTOR = GuiSyncHolder.GLOBAL.layer("player_selector", (nbt) -> new PlayerSelectorDialog());
    
    private PlayerSelector info;
    
    public GuiPlayerSelectorButton(String name, PlayerSelector info) {
        super(name, null);
        pressed = x -> {
            PlayerSelectorDialog layer = PLAYER_SELECTOR.open(getIntegratedParent(), new CompoundTag());
            layer.button = this;
            layer.init();
        };
        setTitle(Component.literal(getLabelText(info)));
        this.info = info;
    }
    
    public void set(PlayerSelector info) {
        this.info = info;
        setTitle(Component.literal(getLabelText(info)));
        raiseEvent(new GuiControlChangedEvent(this));
    }
    
    public static String getLabelText(PlayerSelector value) {
        String info = value.info();
        if (info.length() > 25)
            info = info.substring(22) + "...";
        return info;
    }
    
    public PlayerSelector get() {
        return info;
    }
    
}
