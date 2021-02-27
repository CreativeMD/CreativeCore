package team.creative.creativecore.common.gui.controls;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.SoundEvents;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleFace;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.util.math.Rect;
import team.creative.creativecore.common.util.math.SmoothValue;

public class GuiScrollBox extends GuiParent {
    
    public int maxScroll = 0;
    public SmoothValue scrolled = new SmoothValue(200);
    public float scaleFactor;
    public boolean dragged;
    public int scrollbarWidth = 3;
    protected int initalWidth;
    protected int initalHeight;
    
    public GuiScrollBox(String name, int x, int y, int width, int height) {
        this(name, x, y, width, height, 1F);
    }
    
    public GuiScrollBox(String name, int x, int y, int width, int height, float scaleFactor) {
        super(name, x, y, width, height);
        this.scaleFactor = scaleFactor;
        this.initalWidth = width;
        this.initalHeight = height;
    }
    
    @Override
    public float getScaleFactor() {
        return scaleFactor;
    }
    
    @Override
    public double getOffsetY() {
        return -scrolled.current();
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.NESTED;
    }
    
    public void onScrolled() {
        if (this.scrolled.aimed() < 0)
            this.scrolled.set(0);
        if (this.scrolled.aimed() > maxScroll)
            this.scrolled.set(maxScroll);
    }
    
    @Override
    public boolean mouseScrolled(double x, double y, double scrolled) {
        if (super.mouseScrolled(x, y, scrolled))
            return true;
        this.scrolled.set(this.scrolled.aimed() - scrolled * 10);
        onScrolled();
        return true;
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (button == 0 && getWidth() - x <= scrollbarWidth && needsScrollbar()) {
            playSound(SoundEvents.UI_BUTTON_CLICK);
            dragged = true;
            return true;
        }
        return super.mouseClicked(x, y, button);
    }
    
    @Override
    public void mouseMoved(double x, double y) {
        if (dragged) {
            GuiStyle style = getStyle();
            ControlFormatting formatting = getControlFormatting();
            int completeHeight = getHeight() - style.getBorder(formatting.border) * 2;
            
            int scrollThingHeight = Math.max(10, Math.min(completeHeight, (int) ((float) completeHeight / lastRenderedHeight * completeHeight)));
            if (lastRenderedHeight < completeHeight)
                scrollThingHeight = completeHeight;
            
            double percent = (y - this.getY()) / (completeHeight - scrollThingHeight);
            this.scrolled.set((int) (percent * maxScroll));
            onScrolled();
        }
        super.mouseMoved(x, y);
    }
    
    @Override
    public void mouseReleased(double x, double y, int button) {
        super.mouseReleased(x, y, button);
        dragged = false;
    }
    
    public boolean needsScrollbar() {
        return lastRenderedHeight > this.getHeight() - getContentOffset() * 2;
    }
    
    @Override
    protected void renderContent(MatrixStack matrix, ControlFormatting formatting, int borderWidth, Rect controlRect, Rect realRect, int mouseX, int mouseY) {
        super.renderContent(matrix, formatting, borderWidth, controlRect, realRect, mouseX, mouseY);
        realRect.scissor();
        GuiStyle style = getStyle();
        
        scrolled.tick();
        
        int completeHeight = getHeight() - style.getBorder(formatting.border) * 2;
        
        int scrollThingHeight = Math.max(10, Math.min(completeHeight, (int) ((float) completeHeight / lastRenderedHeight * completeHeight)));
        if (lastRenderedHeight < completeHeight)
            scrollThingHeight = completeHeight;
        double percent = scrolled.current() / maxScroll;
        
        style.get(ControlStyleFace.CLICKABLE, false).render(matrix, controlRect
                .getWidth() + formatting.padding * 2 - scrollbarWidth + borderWidth, (int) (percent * (completeHeight - scrollThingHeight)) + borderWidth, scrollbarWidth, scrollThingHeight);
        
        maxScroll = Math.max(0, (lastRenderedHeight - completeHeight) + formatting.padding * 2 + 1);
        
    }
    
    @Override
    public void setWidthLayout(int width) {
        setWidth(width);
    }
    
    @Override
    public int getMinWidth() {
        return 20;
    }
    
    @Override
    public int getPreferredWidth() {
        return initalWidth;
    }
    
    @Override
    public void setHeightLayout(int height) {
        setHeight(height);
    }
    
    @Override
    public int getMinHeight() {
        return 10;
    }
    
    @Override
    public int getPreferredHeight() {
        return initalHeight;
    }
    
}
