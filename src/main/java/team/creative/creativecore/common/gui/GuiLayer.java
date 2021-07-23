package team.creative.creativecore.common.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.GuiStyle;

public abstract class GuiLayer extends GuiParent {
    
    public final GuiStyle style;
    
    public GuiLayer(String name, int width, int height) {
        super(name, 0, 0, width, height);
        this.style = GuiStyle.getStyle(name);
    }
    
    @Override
    public void init() {
        create();
        super.init();
        initiateLayoutUpdate();
    }
    
    public void reinit() {
        super.init();
        initiateLayoutUpdate();
    }
    
    @Override
    public void initiateLayoutUpdate() {
        updateLayout();
    }
    
    public abstract void create();
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.GUI;
    }
    
    @Override
    public String getNestedName() {
        return "gui." + super.getNestedName();
    }
    
    @Override
    public GuiLayer getLayer() {
        return this;
    }
    
    @Override
    public GuiStyle getStyle() {
        return style;
    }
    
    public boolean closeLayerUsingEscape() {
        return true;
    }
    
    @OnlyIn(value = Dist.CLIENT)
    public Options getSettings() {
        return Minecraft.getInstance().options;
    }
    
    @OnlyIn(value = Dist.CLIENT)
    public boolean hasGrayBackground() {
        return true;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            if (closeLayerUsingEscape())
                closeTopLayer();
            return true;
        }
        if (super.keyPressed(keyCode, scanCode, modifiers))
            return true;
        if (keyCode == getSettings().keyInventory.getKey().getValue()) {
            closeTopLayer();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean hasLayer() {
        return true;
    }
    
    @Override
    public int getPreferredWidth() {
        return 0;
    }
    
    @Override
    public int getPreferredHeight() {
        return 0;
    }
    
    @Override
    public void setWidthLayout(int width) {}
    
    @Override
    public int getMinWidth() {
        return 0;
    }
    
    @Override
    public void setHeightLayout(int height) {}
    
    @Override
    public int getMinHeight() {
        return 0;
    }
}
