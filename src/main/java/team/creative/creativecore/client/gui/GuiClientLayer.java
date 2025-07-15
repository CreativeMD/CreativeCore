package team.creative.creativecore.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import team.creative.creativecore.client.gui.manager.GuiClientManager;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.GuiLayer.GuiLayerDistHandler;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.type.itr.FunctionIterator;

public class GuiClientLayer extends GuiClientParent<GuiLayer<?>> implements GuiLayerDistHandler {
    
    public static final int MINIMUM_OUTER_SPACING = 10;
    
    public GuiStyle style;
    
    public GuiClientLayer(GuiLayer layer) {
        super(layer);
        this.style = GuiStyle.getStyle(control.name);
    }
    
    public Iterable<GuiClientManager> managers() {
        return new FunctionIterator<>(control.managers(), x -> (GuiClientManager) x.dist);
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.GUI;
    }
    
    public int getWidth() {
        return rect.getWidth();
    }
    
    public int getHeight() {
        return rect.getHeight();
    }
    
    @Override
    public void reflow() {
        Rect screen = getScreenRect();
        int screenWidth = (int) screen.getWidth() - getContentOffset() * 2 - MINIMUM_OUTER_SPACING;
        int fixedWidth = -1;
        int width = 0;
        
        if (preferred != null)
            width = fixedWidth = preferred.preferredWidth(this, screenWidth);
        if (fixedWidth == -1)
            if (isExpandableX())
                width = screenWidth;
            else
                width = Math.min(screenWidth, preferredWidth(screenWidth));
        if (preferred != null) {
            int minWidth = preferred.minWidth(this, screenWidth);
            if (minWidth != -1)
                width = Math.max(width, minWidth);
            int maxWidth = preferred.maxWidth(this, screenWidth);
            if (maxWidth != -1)
                width = Math.min(width, maxWidth);
        }
        rect.setRight(width + getContentOffset() * 2);
        flowX(width, preferredWidth(fixedWidth != -1 ? fixedWidth : screenWidth));
        
        int screenHeight = (int) screen.getHeight() - getContentOffset() * 2 - MINIMUM_OUTER_SPACING;
        int fixedHeight = -1;
        int height = 0;
        if (preferred != null)
            height = fixedHeight = preferred.preferredHeight(this, width, screenHeight);
        if (fixedHeight == -1)
            if (isExpandableY())
                height = screenHeight;
            else
                height = Math.min(screenHeight, preferredHeight(width, screenHeight));
        if (preferred != null) {
            int minHeight = preferred.minHeight(this, width, screenHeight);
            if (minHeight != -1)
                height = Math.max(height, minHeight);
            int maxHeight = preferred.maxHeight(this, width, screenHeight);
            if (maxHeight != -1)
                height = Math.min(height, maxHeight);
        }
        rect.setBottom(height + getContentOffset() * 2);
        flowY(width, height, preferredHeight(width, fixedHeight != -1 ? fixedHeight : screenHeight));
    }
    
    @Override
    public GuiStyle getStyle() {
        return style;
    }
    
    @Override
    protected void renderContent(GuiGraphics graphics, Rect controlRect, Rect realRect, double scale, int mouseX, int mouseY) {
        super.renderContent(graphics, controlRect, realRect, scale, mouseX, mouseY);
        
        for (GuiClientManager manager : managers())
            manager.renderOverlay(graphics, this, mouseX - (int) controlRect.minX, mouseY - (int) controlRect.minY);
    }
    
    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY) {}
    
    public Options getSettings() {
        return Minecraft.getInstance().options;
    }
    
    public boolean hasGrayBackground() {
        return true;
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (!this.rect.inside(x, y) && !isMouseOverHovered(x, y)) {
            looseFocus();
            for (GuiClientManager manager : managers())
                manager.mouseClickedOutside(x, y);
            return false;
        }
        return super.mouseClicked(x, y, button);
    }
    
    @Override
    public void mouseReleased(double x, double y, int button) {
        super.mouseReleased(x, y, button);
        
        for (GuiClientManager manager : managers())
            manager.mouseReleased(x, y, button);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            if (control.closeLayerUsingEscape())
                control.closeTopLayer();
            return true;
        }
        if (super.keyPressed(keyCode, scanCode, modifiers))
            return true;
        if (getSettings().keyInventory.matches(keyCode, scanCode)) {
            control.closeTopLayer();
            return true;
        }
        return false;
    }
    
}
