package team.creative.creativecore.common.config.gui;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.handler.GuiLayerHandler;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;
import team.creative.creativecore.common.gui.packet.LayerOpenPacket;
import team.creative.creativecore.common.util.ingredient.CreativeIngredient;
import team.creative.creativecore.common.util.ingredient.CreativeIngredientBlock;
import team.creative.creativecore.common.util.ingredient.CreativeIngredientBlockTag;
import team.creative.creativecore.common.util.ingredient.CreativeIngredientFuel;
import team.creative.creativecore.common.util.ingredient.CreativeIngredientItem;
import team.creative.creativecore.common.util.ingredient.CreativeIngredientItemStack;
import team.creative.creativecore.common.util.ingredient.CreativeIngredientItemTag;
import team.creative.creativecore.common.util.ingredient.CreativeIngredientMaterial;
import team.creative.creativecore.common.util.mc.MaterialUtils;
import team.creative.creativecore.common.util.text.TextBuilder;

public class GuiInfoStackButton extends GuiButton {
    
    private CreativeIngredient info;
    
    public GuiInfoStackButton(String name, CreativeIngredient info) {
        super(name, null);
        pressed = button -> {
            FullItemDialogGuiLayer layer = (FullItemDialogGuiLayer) this.getParent().openLayer(new LayerOpenPacket("info", new CompoundTag()));
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
            if (value instanceof CreativeIngredientBlock)
                text.text("Block: " + ChatFormatting.YELLOW + ((CreativeIngredientBlock) value).block.getRegistryName().toString());
            else if (value instanceof CreativeIngredientBlockTag)
                text.text("Blocktag: " + ChatFormatting.YELLOW + BlockTags.getAllTags().getId(((CreativeIngredientBlockTag) value).tag).toString());
            else if (value instanceof CreativeIngredientItem)
                text.text("Item: " + ChatFormatting.YELLOW + ((CreativeIngredientItem) value).item.getRegistryName().toString());
            else if (value instanceof CreativeIngredientItemTag)
                text.text("Itemtag: " + ChatFormatting.YELLOW + ItemTags.getAllTags().getId(((CreativeIngredientItemTag) value).tag).toString());
            else if (value instanceof CreativeIngredientItemStack)
                text.text("Stack: " + ChatFormatting.YELLOW).add(((CreativeIngredientItemStack) value).stack.getDisplayName());
            else if (value instanceof CreativeIngredientMaterial)
                text.text("Material: " + ChatFormatting.YELLOW + MaterialUtils.getName(((CreativeIngredientMaterial) value).material));
            else if (value instanceof CreativeIngredientFuel)
                text.text("Fuel");
            else
                text.text("No information");
        }
        return text.build();
        
    }
    
    public CreativeIngredient get() {
        return info;
    }
    
    static {
        GuiLayerHandler.REGISTRY.register("info", new GuiLayerHandler() {
            
            @Override
            public GuiLayer create(IGuiIntegratedParent parent, CompoundTag nbt) {
                return new FullItemDialogGuiLayer();
            }
        });
    }
}
