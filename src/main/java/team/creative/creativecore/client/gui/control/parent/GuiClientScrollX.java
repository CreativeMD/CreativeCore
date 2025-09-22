package team.creative.creativecore.client.gui.control.parent;

import org.joml.Matrix3x2fStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.client.gui.GuiClientParent;
import team.creative.creativecore.common.gui.control.parent.GuiScrollX;
import team.creative.creativecore.common.gui.control.parent.GuiScrollX.GuiScrollXDist;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleFace;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.gui.style.display.StyleDisplay;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.math.vec.SmoothValue;

public class GuiClientScrollX<T extends GuiScrollX> extends GuiClientParent<T> implements GuiScrollXDist {
    
    public int maxScroll = 0;
    public SmoothValue scrolled = new SmoothValue(200);
    public boolean dragged;
    public int scrollbarHeight = 3;
    public boolean hoveredScroll;
    private int cachedWidth;
    
    public GuiClientScrollX(T control) {
        super(control);
    }
    
    @Override
    public void setHovered() {
        this.hoveredScroll = true;
    }
    
    @Override
    public void setHover(boolean hover) {
        this.hoveredScroll = hover;
    }
    
    @Override
    public double getOffsetX() {
        return -scrolled.current();
    }
    
    @Override
    protected ControlFormatting defaultFormatting() {
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
        scroll(scrolled);
        return true;
    }
    
    public void scroll(double scrolled) {
        this.scrolled.set(this.scrolled.aimed() - scrolled * 10);
        onScrolled();
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (button == 0 && rect.getHeight() - y <= scrollbarHeight && needsScrollbar()) {
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
            int completeWidth = rect.getWidth() - style.getBorder(formatting.border()) * 2;
            
            int scrollThingWidth = Math.max(10, Math.min(completeWidth, (int) ((float) completeWidth / cachedWidth * completeWidth)));
            if (cachedWidth < completeWidth)
                scrollThingWidth = completeWidth;
            
            double percent = (x) / (completeWidth - scrollThingWidth);
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
        return cachedWidth > rect.getContentWidth();
    }
    
    @Override
    protected void renderContent(GuiGraphics graphics, ControlFormatting formatting, int borderWidth, Rect controlRect, Rect realRect, double scale, int mouseX, int mouseY) {
        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        super.renderContent(graphics, formatting, borderWidth, controlRect, realRect, scale, mouseX, mouseY);
        pose.popMatrix();
        
        if (!needsScrollbar() && hoveredScroll)
            return;
        
        //if (hoveredScroll)
        //    RenderSystem.disableDepthTest();
        // TODO 1.21.5 YET TO BE TESTED
        
        float controlInvScale = (float) scaleFactorInv();
        pose.scale(controlInvScale, controlInvScale);
        
        scissor(graphics, realRect);
        GuiStyle style = getStyle();
        
        scrolled.tick();
        
        int completeWidth = rect.getWidth() - borderWidth * 2;
        
        int scrollThingWidth = Math.max(10, Math.min(completeWidth, (int) ((float) completeWidth / cachedWidth * completeWidth)));
        if (scrollThingWidth > completeWidth)
            scrollThingWidth = completeWidth;
        double percent = scrolled.current() / maxScroll;
        
        StyleDisplay display = hoveredScroll ? style.disabled : style.get(ControlStyleFace.CLICKABLE, false);
        display.render(graphics, (int) (percent * (completeWidth - scrollThingWidth)) + borderWidth, (rect.getHeight() - borderWidth * scrollbarHeight) - borderWidth,
            scrollThingWidth, scrollbarHeight);
        
        maxScroll = Math.max(0, (cachedWidth - completeWidth) + formatting.padding() * 2 + 1);
        
        float controlScale = (float) scaleFactor();
        pose.scale(controlScale, controlScale);
        
        //if (hoveredScroll)
        //    RenderSystem.enableDepthTest();
    }
    
    @Override
    protected int minWidth(int availableWidth) {
        return 10;
    }
    
    @Override
    protected int minHeight(int width, int availableHeight) {
        return 10;
    }
    
    @Override
    protected boolean endlessX() {
        return true;
    }
    
    @Override
    public void flowX(int width, int preferred) {
        super.flowX(width, preferred);
        this.cachedWidth = width;
    }
    
    @Override
    public void flowY(int width, int height, int preferred) {
        if (!hoveredScroll)
            height -= scrollbarHeight;
        super.flowY(width, height, preferred);
    }
}
