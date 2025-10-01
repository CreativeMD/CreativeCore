package team.creative.creativecore.client.gui.control.parent;

import org.joml.Matrix3x2fStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.client.gui.GuiClientControl;
import team.creative.creativecore.client.gui.GuiClientParent;
import team.creative.creativecore.common.gui.control.parent.GuiScrollXY;
import team.creative.creativecore.common.gui.control.parent.GuiScrollXY.GuiScrollXYDist;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleFace;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.math.vec.SmoothValue;

public class GuiClientScrollXY<T extends GuiScrollXY> extends GuiClientParent<T> implements GuiScrollXYDist {
    
    public int maxScrollX = 0;
    public SmoothValue scrolledX = new SmoothValue(200);
    public boolean draggedX;
    
    public int maxScrollY = 0;
    public SmoothValue scrolledY = new SmoothValue(200);
    public boolean draggedY;
    
    public int scrollbarThickness = 3;
    public ControlStyleFace scrollbarFace = ControlStyleFace.CLICKABLE;
    
    public boolean alternativeScrolling = false;
    
    protected int cachedWidth;
    protected int cachedHeight;
    
    public GuiClientScrollXY(T control) {
        super(control);
    }
    
    @Override
    public double getOffsetX() {
        return -scrolledX.current();
    }
    
    @Override
    public double getOffsetY() {
        return -scrolledY.current();
    }
    
    @Override
    protected ControlFormatting defaultFormatting() {
        return ControlFormatting.NESTED;
    }
    
    public void onScrolledX() {
        if (this.scrolledX.aimed() < 0)
            this.scrolledX.set(0);
        if (this.scrolledX.aimed() > maxScrollX)
            this.scrolledX.set(maxScrollX);
    }
    
    public void onScrolledY() {
        if (this.scrolledY.aimed() < 0)
            this.scrolledY.set(0);
        if (this.scrolledY.aimed() > maxScrollY)
            this.scrolledY.set(maxScrollY);
    }
    
    @Override
    public boolean mouseScrolled(double x, double y, double scrolled) {
        if (super.mouseScrolled(x, y, scrolled))
            return true;
        
        scroll(scrolled);
        return true;
    }
    
    public void scroll(double scrolled) {
        if (alternativeScrolling) {
            if (Minecraft.getInstance().hasShiftDown()) {
                if (needsScrollbarX()) {
                    this.scrolledX.set(this.scrolledX.aimed() - scrolled * 10);
                    onScrolledX();
                }
                return;
            }
            
            this.scrolledY.set(this.scrolledY.aimed() - scrolled * 10);
            onScrolledY();
            return;
        }
        
        boolean shouldScrollY = needsScrollbarY();
        if (shouldScrollY)
            if (scrolled > 0 && this.scrolledY.aimed() == 0)
                shouldScrollY = false;
            else if (scrolled < 0 && this.scrolledY.aimed() == maxScrollY)
                shouldScrollY = false;
            
        if (shouldScrollY) {
            this.scrolledY.set(this.scrolledY.aimed() - scrolled * 10);
            onScrolledY();
            return;
        }
        
        this.scrolledX.set(this.scrolledX.aimed() - scrolled * 10);
        onScrolledX();
    }
    
    @Override
    public boolean mouseClicked(double x, double y, MouseButtonInfo info) {
        if (info.button() == 0 && rect.getHeight() - y <= scrollbarThickness && needsScrollbarX()) {
            playSound(SoundEvents.UI_BUTTON_CLICK);
            draggedX = true;
            return true;
        }
        if (info.button() == 0 && rect.getWidth() - x <= scrollbarThickness && needsScrollbarY()) {
            playSound(SoundEvents.UI_BUTTON_CLICK);
            draggedY = true;
            return true;
        }
        return super.mouseClicked(x, y, info);
    }
    
    @Override
    public void mouseMoved(double x, double y) {
        if (draggedX) {
            GuiStyle style = getStyle();
            ControlFormatting formatting = getControlFormatting();
            int completeWidth = rect.getWidth() - style.getBorder(formatting.border()) * 2;
            
            int scrollThingWidth = Math.max(10, Math.min(completeWidth, (int) ((float) completeWidth / cachedWidth * completeWidth)));
            if (cachedWidth < completeWidth)
                scrollThingWidth = completeWidth;
            
            double percent = (x) / (completeWidth - scrollThingWidth);
            this.scrolledX.set((int) (percent * maxScrollX));
            onScrolledX();
        }
        if (draggedY) {
            GuiStyle style = getStyle();
            ControlFormatting formatting = getControlFormatting();
            int completeHeight = rect.getHeight() - style.getBorder(formatting.border()) * 2;
            
            int scrollThingHeight = Math.max(10, Math.min(completeHeight, (int) ((float) completeHeight / cachedHeight * completeHeight)));
            if (cachedHeight < completeHeight)
                scrollThingHeight = completeHeight;
            
            double percent = (y) / (completeHeight - scrollThingHeight);
            this.scrolledY.set((int) (percent * maxScrollY));
            onScrolledY();
        }
        super.mouseMoved(x, y);
    }
    
    @Override
    public void mouseReleased(double x, double y, MouseButtonInfo info) {
        super.mouseReleased(x, y, info);
        draggedX = draggedY = false;
    }
    
    public boolean needsScrollbarX() {
        return cachedWidth > rect.getContentWidth();
    }
    
    public boolean needsScrollbarY() {
        return cachedHeight > rect.getContentHeight();
    }
    
    @Override
    protected void renderContent(GuiGraphics graphics, ControlFormatting formatting, int borderWidth, Rect controlRect, Rect realRect, double scale, int mouseX, int mouseY) {
        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        super.renderContent(graphics, formatting, borderWidth, controlRect, realRect, scale, mouseX, mouseY);
        pose.popMatrix();
        
        float controlInvScale = (float) scaleFactorInv();
        pose.scale(controlInvScale, controlInvScale);
        
        scissor(graphics, realRect);
        GuiStyle style = getStyle();
        
        scrolledX.tick();
        
        if (needsScrollbarX()) {
            int completeWidth = rect.getWidth() - borderWidth * 2;
            
            int scrollThingWidth = Math.max(10, Math.min(completeWidth, (int) ((float) completeWidth / cachedWidth * completeWidth)));
            if (cachedWidth < completeWidth)
                scrollThingWidth = completeWidth;
            double percent = scrolledX.current() / maxScrollX;
            
            style.get(scrollbarFace, false).render(graphics, (int) (percent * (completeWidth - scrollThingWidth)) + borderWidth, rect
                    .getHeight() - scrollbarThickness - borderWidth, scrollThingWidth, scrollbarThickness);
            
            maxScrollX = Math.max(0, (cachedWidth - completeWidth) + formatting.padding() * 2 + 1);
        }
        
        scrolledY.tick();
        
        if (needsScrollbarY()) {
            int completeHeight = rect.getHeight() - borderWidth * 2;
            
            int scrollThingHeight = Math.max(10, Math.min(completeHeight, (int) ((float) completeHeight / cachedHeight * completeHeight)));
            if (cachedHeight < completeHeight)
                scrollThingHeight = completeHeight;
            double percent = scrolledY.current() / maxScrollY;
            
            style.get(scrollbarFace, false).render(graphics, rect.getWidth() - scrollbarThickness - borderWidth,
                (int) (percent * (completeHeight - scrollThingHeight)) + borderWidth, scrollbarThickness, scrollThingHeight);
            
            maxScrollY = Math.max(0, (cachedHeight - completeHeight) + formatting.padding() * 2 + 1);
        }
        
        float controlScale = (float) scaleFactor();
        pose.scale(controlScale, controlScale);
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
    public void flowX(int width, int preferred) {
        super.flowX(preferred, preferred);
        updateWidth();
    }
    
    protected void updateWidth() {
        int maxX = 0;
        for (GuiClientControl control : controls())
            maxX = Math.max(control.rect.getRight(), maxX);
        cachedWidth = maxX;
    }
    
    @Override
    public void flowY(int width, int height, int preferred) {
        super.flowY(cachedWidth, preferred, preferred);
        updateHeight();
    }
    
    protected void updateHeight() {
        int maxY = 0;
        for (GuiClientControl control : controls())
            maxY = Math.max(control.rect.getBottom(), maxY);
        cachedHeight = maxY;
    }
    
}
