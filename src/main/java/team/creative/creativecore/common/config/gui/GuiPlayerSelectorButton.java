package team.creative.creativecore.common.config.gui;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.handler.GuiLayerHandler;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;
import team.creative.creativecore.common.gui.packet.LayerOpenPacket;
import team.creative.creativecore.common.util.player.PlayerSelector;

public class GuiPlayerSelectorButton extends GuiButton {
    
    private PlayerSelector info;
    
    public GuiPlayerSelectorButton(String name, PlayerSelector info) {
        super(name, null);
        pressed = x -> {
            PlayerSelectorDialog layer = (PlayerSelectorDialog) this.getParent().openLayer(new LayerOpenPacket("player", new CompoundTag()));
            layer.button = this;
            layer.init();
        };
        setTitle(new TextComponent(getLabelText(info)));
        this.info = info;
    }
    
    public void set(PlayerSelector info) {
        this.info = info;
        setTitle(new TextComponent(getLabelText(info)));
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
    
    static {
        GuiLayerHandler.REGISTRY.register("player", new GuiLayerHandler() {
            
            @Override
            public GuiLayer create(IGuiIntegratedParent parent, CompoundTag nbt) {
                return new PlayerSelectorDialog();
            }
        });
    }
    
}
