package team.creative.creativecore.common.config.gui;

import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.creator.GuiLayerHandler;
import team.creative.creativecore.common.gui.packet.LayerOpenPacket;
import team.creative.creativecore.common.util.ingredient.CreativeIngredient;
import team.creative.creativecore.common.util.text.TextBuilder;

public class GuiInfoStackButton extends GuiButton {
    
    public static final GuiLayerHandler INFO_LAYER = (parent, nbt) -> new FullItemDialogGuiLayer();
    
    private CreativeIngredient info;
    
    public GuiInfoStackButton(String name, CreativeIngredient info) {
        super(name, null);
        pressed = button -> {
            FullItemDialogGuiLayer layer = (FullItemDialogGuiLayer) this.getParent().openLayer(new LayerOpenPacket(INFO_LAYER, new CompoundTag()));
            layer.button = this;
            layer.init();
        };
        this.info = info;
        setTitle(getLabelText(info));
    }
    
    public void set(CreativeIngredient info) {
        this.info = info;
        setTitle(getLabelText(info));
        raiseEvent(new GuiControlChangedEvent(this));
    }
    
    public static List<Component> getLabelText(CreativeIngredient value) {
        TextBuilder text = new TextBuilder();
        if (value != null) {
            text.stack(value.getExample());
            text.add(value.descriptionDetail());
        }
        return text.build();
        
    }
    
    public CreativeIngredient get() {
        return info;
    }
    
}
