package team.creative.creativecore.common.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.gui.controls.parent.GuiBoxX;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.util.math.geo.Rect;

public abstract class GuiLayer extends GuiBoxX {
    
    public final GuiStyle style;
    public final Rect rect;
    
    public GuiLayer(String name, int width, int height) {
        super(name, width, height);
        this.rect = new Rect(0, 0, width, height);
        this.style = GuiStyle.getStyle(name);
    }
    
    public int getWidth() {
        return preferredWidth;
    }
    
    public int getHeight() {
        return preferredHeight;
    }
    
    @Override
    public void init() {
        create();
        super.init();
        reflow();
    }
    
    public void reinit() {
        super.init();
        reflow();
    }
    
    @Override
    public void reflow() {
        flowX((int) rect.getWidth() - getContentOffset() * 2, preferredWidth());
        flowY((int) rect.getHeight() - getContentOffset() * 2, preferredHeight());
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
}
