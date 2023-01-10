package team.creative.creativecore.common.config.gui;

import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.sync.GuiSyncGlobalLayer;
import team.creative.creativecore.common.gui.sync.GuiSyncHolder;
import team.creative.creativecore.common.util.ingredient.CreativeIngredient;
import team.creative.creativecore.common.util.text.TextBuilder;

public class GuiInfoStackButton extends GuiButton {
    
    public static final GuiSyncGlobalLayer<GuiInfoStackButton, FullItemDialogGuiLayer> ITEM_DIALOG = GuiSyncHolder.GLOBAL
            .layer("item_dialog", (c, t) -> new FullItemDialogGuiLayer());
    
    private CreativeIngredient info;
    
    public GuiInfoStackButton(String name, CreativeIngredient info) {
        super(name, null);
        pressed = button -> {
            FullItemDialogGuiLayer layer = ITEM_DIALOG.open(this, new CompoundTag());
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
