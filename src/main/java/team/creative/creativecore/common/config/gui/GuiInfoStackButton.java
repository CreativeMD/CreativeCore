package team.creative.creativecore.common.config.gui;

import java.util.List;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import team.creative.creativecore.common.gui.controls.GuiButtonFixed;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
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

public class GuiInfoStackButton extends GuiButtonFixed {
    
    private CreativeIngredient info;
    
    public GuiInfoStackButton(String name, int x, int y, int width, int height, CreativeIngredient info) {
        super(name, x, y, width, height, button -> {
            /*
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setBoolean("dialog", true);
            SubGui gui = new FullItemDialogGuiLayer(this, false);
            gui.container = new SubContainerEmpty(getPlayer());
            gui.gui = getGui().gui;
            getGui().gui.addLayer(gui);
            PacketHandler.sendPacketToServer(new GuiLayerPacket(nbt, gui.gui.getLayers().size() - 1, false));
            gui.onOpened();*/
        });
        this.info = info;
        setTitle(getLabelText(info));
    }
    
    public void set(CreativeIngredient info) {
        this.info = info;
        raiseEvent(new GuiControlChangedEvent(this));
    }
    
    public static List<ITextComponent> getLabelText(CreativeIngredient value) {
        TextBuilder text = new TextBuilder();
        if (value != null) {
            text.stack(value.getExample());
            if (value instanceof CreativeIngredientBlock)
                text.text("Block: " + TextFormatting.YELLOW + ((CreativeIngredientBlock) value).block.getRegistryName().toString());
            else if (value instanceof CreativeIngredientBlockTag)
                text.text("Blocktag: " + TextFormatting.YELLOW + BlockTags.getCollection().getDirectIdFromTag(((CreativeIngredientBlockTag) value).tag).toString());
            else if (value instanceof CreativeIngredientItem)
                text.text("Item: " + TextFormatting.YELLOW + ((CreativeIngredientItem) value).item.getRegistryName().toString());
            else if (value instanceof CreativeIngredientItemTag)
                text.text("Itemtag: " + TextFormatting.YELLOW + ItemTags.getCollection().getDirectIdFromTag(((CreativeIngredientItemTag) value).tag).toString());
            else if (value instanceof CreativeIngredientItemStack)
                text.text("Stack: " + TextFormatting.YELLOW).add(((CreativeIngredientItemStack) value).stack.getDisplayName());
            else if (value instanceof CreativeIngredientMaterial)
                text.text("Material: " + TextFormatting.YELLOW + MaterialUtils.getName(((CreativeIngredientMaterial) value).material));
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
    
}
