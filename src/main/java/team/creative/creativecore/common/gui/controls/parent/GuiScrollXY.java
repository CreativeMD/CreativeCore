package team.creative.creativecore.common.gui.controls.parent;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleFace;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.math.vec.SmoothValue;

public class GuiScrollXY extends GuiParent {
    
    public int maxScrollX = 0;
    public SmoothValue scrolledX = new SmoothValue(200);
    public boolean draggedX;
    
    public int maxScrollY = 0;
    public SmoothValue scrolledY = new SmoothValue(200);
    public boolean draggedY;
    
    public double scaleFactor = 1;
    public int scrollbarThickness = 3;
    
    protected int cachedWidth;
    protected int cachedHeight;
    
    public GuiScrollXY() {
        this("");
    }
    
    public GuiScrollXY(String name) {
        this(name, GuiFlow.STACK_X);
    }
    
    public GuiScrollXY(String name, GuiFlow flow) {
        super(name, flow);
    }
    
    public GuiScrollXY(String name, int width, int height) {
        this(name, GuiFlow.STACK_X, width, height);
    }
    
    public GuiScrollXY(String name, GuiFlow flow, int width, int height) {
        super(name, flow, width, height);
    }
    
    @Override
    public double getScaleFactor() {
        return scaleFactor;
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
    public ControlFormatting getControlFormatting() {
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
    public boolean mouseScrolled(Rect rect, double x, double y, double scrolled) {
        if (super.mouseScrolled(rect, x, y, scrolled))
            return true;
        
        boolean shouldScrollY = needsScrollbarY(rect);
        if (shouldScrollY)
            if (scrolled > 0 && this.scrolledY.aimed() == 0)
                shouldScrollY = false;
            else if (scrolled < 0 && this.scrolledY.aimed() == maxScrollY)
                shouldScrollY = false;
            
        if (shouldScrollY) {
            this.scrolledY.set(this.scrolledY.aimed() - scrolled * 10);
            onScrolledY();
            return true;
        }
        
        this.scrolledX.set(this.scrolledX.aimed() - scrolled * 10);
        onScrolledX();
        return true;
    }
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        if (button == 0 && rect.getHeight() - y <= scrollbarThickness && needsScrollbarX(rect)) {
            playSound(SoundEvents.UI_BUTTON_CLICK);
            draggedX = true;
            return true;
        }
        if (button == 0 && rect.getWidth() - x <= scrollbarThickness && needsScrollbarY(rect)) {
            playSound(SoundEvents.UI_BUTTON_CLICK);
            draggedY = true;
            return true;
        }
        return super.mouseClicked(rect, x, y, button);
    }
    
    @Override
    public void mouseMoved(Rect rect, double x, double y) {
        if (draggedX) {
            GuiStyle style = getStyle();
            ControlFormatting formatting = getControlFormatting();
            int completeWidth = (int) (rect.getWidth() - style.getBorder(formatting.border) * 2);
            
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
            int completeHeight = (int) (rect.getHeight() - style.getBorder(formatting.border) * 2);
            
            int scrollThingHeight = Math.max(10, Math.min(completeHeight, (int) ((float) completeHeight / cachedHeight * completeHeight)));
            if (cachedHeight < completeHeight)
                scrollThingHeight = completeHeight;
            
            double percent = (y) / (completeHeight - scrollThingHeight);
            this.scrolledY.set((int) (percent * maxScrollY));
            onScrolledY();
        }
        super.mouseMoved(rect, x, y);
    }
    
    @Override
    public void mouseReleased(Rect rect, double x, double y, int button) {
        super.mouseReleased(rect, x, y, button);
        draggedX = draggedY = false;
    }
    
    public boolean needsScrollbarX(Rect rect) {
        return cachedWidth > rect.getWidth() - getContentOffset() * 2;
    }
    
    public boolean needsScrollbarY(Rect rect) {
        return cachedHeight > rect.getHeight() - getContentOffset() * 2;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderContent(PoseStack matrix, GuiChildControl control, ControlFormatting formatting, int borderWidth, Rect controlRect, Rect realRect, int mouseX, int mouseY) {
        super.renderContent(matrix, control, formatting, borderWidth, controlRect, realRect, mouseX, mouseY);
        realRect.scissor();
        GuiStyle style = getStyle();
        
        scrolledX.tick();
        
        if (needsScrollbarX(control.rect)) {
            int completeWidth = control.getWidth() - style.getBorder(formatting.border) * 2;
            
            int scrollThingWidth = Math.max(10, Math.min(completeWidth, (int) ((float) completeWidth / cachedWidth * completeWidth)));
            if (cachedWidth < completeWidth)
                scrollThingWidth = completeWidth;
            double percent = scrolledX.current() / maxScrollX;
            
            style.get(ControlStyleFace.CLICKABLE, false).render(matrix, (int) (percent * (completeWidth - scrollThingWidth)) + borderWidth, controlRect
                    .getHeight() + formatting.padding * 2 - scrollbarThickness + borderWidth, scrollThingWidth, scrollbarThickness);
            
            maxScrollX = Math.max(0, (cachedWidth - completeWidth) + formatting.padding * 2 + 1);
        }
        
        scrolledY.tick();
        
        if (needsScrollbarY(control.rect)) {
            int completeHeight = control.getHeight() - style.getBorder(formatting.border) * 2;
            
            int scrollThingHeight = Math.max(10, Math.min(completeHeight, (int) ((float) completeHeight / cachedHeight * completeHeight)));
            if (cachedHeight < completeHeight)
                scrollThingHeight = completeHeight;
            double percent = scrolledY.current() / maxScrollY;
            
            style.get(ControlStyleFace.CLICKABLE, false).render(matrix, controlRect
                    .getWidth() + formatting.padding * 2 - scrollbarThickness + borderWidth, (int) (percent * (completeHeight - scrollThingHeight)) + borderWidth, scrollbarThickness, scrollThingHeight);
            
            maxScrollY = Math.max(0, (cachedHeight - completeHeight) + formatting.padding * 2 + 1);
        }
        
    }
    
    @Override
    public int getMinWidth() {
        return 10;
    }
    
    @Override
    public int getMinHeight(int width) {
        return 10;
    }
    
    @Override
    public void flowX(int width, int preferred) {
        super.flowX(preferred, preferred);
        updateWidth();
    }
    
    protected void updateWidth() {
        int maxX = 0;
        for (GuiChildControl child : controls)
            maxX = Math.max((int) child.rect.maxX, maxX);
        cachedWidth = maxX;
    }
    
    @Override
    public void flowY(int width, int height, int preferred) {
        super.flowY(cachedWidth, preferred, preferred);
        updateHeight();
    }
    
    protected void updateHeight() {
        int maxY = 0;
        for (GuiChildControl child : controls)
            maxY = Math.max((int) child.rect.maxY, maxY);
        cachedHeight = maxY;
    }
}
