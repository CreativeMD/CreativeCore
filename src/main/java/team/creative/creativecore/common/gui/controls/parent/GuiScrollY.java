package team.creative.creativecore.common.gui.controls.parent;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleFace;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.math.vec.SmoothValue;

public class GuiScrollY extends GuiParent {
    
    public int maxScroll = 0;
    public SmoothValue scrolled = new SmoothValue(200);
    public double scaleFactor;
    public boolean dragged;
    public int scrollbarWidth = 3;
    
    protected int cachedHeight;
    
    public GuiScrollY() {
        this("");
    }
    
    public GuiScrollY(String name) {
        super(name, GuiFlow.STACK_Y);
        this.scaleFactor = 1;
    }
    
    public GuiScrollY(String name, int width, int height) {
        this(name, width, height, 1);
    }
    
    public GuiScrollY(String name, int width, int height, float scaleFactor) {
        super(name, GuiFlow.STACK_Y, width, height);
        this.scaleFactor = scaleFactor;
    }
    
    @Override
    public GuiScrollY setExpandable() {
        return (GuiScrollY) super.setExpandable();
    }
    
    @Override
    public double getScaleFactor() {
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
    public boolean mouseScrolled(Rect rect, double x, double y, double scrolled) {
        if (super.mouseScrolled(rect, x, y, scrolled))
            return true;
        this.scrolled.set(this.scrolled.aimed() - scrolled * 10);
        onScrolled();
        return true;
    }
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        if (button == 0 && rect.getWidth() - x <= scrollbarWidth && needsScrollbar(rect)) {
            playSound(SoundEvents.UI_BUTTON_CLICK);
            dragged = true;
            return true;
        }
        return super.mouseClicked(rect, x, y, button);
    }
    
    @Override
    public void mouseMoved(Rect rect, double x, double y) {
        if (dragged) {
            GuiStyle style = getStyle();
            ControlFormatting formatting = getControlFormatting();
            int completeHeight = (int) (rect.getHeight() - style.getBorder(formatting.border) * 2);
            
            int scrollThingHeight = Math.max(10, Math.min(completeHeight, (int) ((float) completeHeight / cachedHeight * completeHeight)));
            if (cachedHeight < completeHeight)
                scrollThingHeight = completeHeight;
            
            double percent = (y) / (completeHeight - scrollThingHeight);
            this.scrolled.set((int) (percent * maxScroll));
            onScrolled();
        }
        super.mouseMoved(rect, x, y);
    }
    
    @Override
    public void mouseReleased(Rect rect, double x, double y, int button) {
        super.mouseReleased(rect, x, y, button);
        dragged = false;
    }
    
    public boolean needsScrollbar(Rect rect) {
        return cachedHeight > rect.getHeight() - getContentOffset() * 2;
    }
    
    @Override
    protected void renderContent(PoseStack matrix, GuiChildControl control, ControlFormatting formatting, int borderWidth, Rect controlRect, Rect realRect, int mouseX, int mouseY) {
        super.renderContent(matrix, control, formatting, borderWidth, controlRect, realRect, mouseX, mouseY);
        realRect.scissor();
        GuiStyle style = getStyle();
        
        scrolled.tick();
        
        int completeHeight = control.getHeight() - style.getBorder(formatting.border) * 2;
        
        int scrollThingHeight = Math.max(10, Math.min(completeHeight, (int) ((float) completeHeight / cachedHeight * completeHeight)));
        if (cachedHeight < completeHeight)
            scrollThingHeight = completeHeight;
        double percent = scrolled.current() / maxScroll;
        
        style.get(ControlStyleFace.CLICKABLE, false).render(matrix, controlRect
                .getWidth() + formatting.padding * 2 - scrollbarWidth + borderWidth, (int) (percent * (completeHeight - scrollThingHeight)) + borderWidth, scrollbarWidth, scrollThingHeight);
        
        maxScroll = Math.max(0, (cachedHeight - completeHeight) + formatting.padding * 2 + 1);
        
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
        super.flowX(width - scrollbarWidth, preferred);
    }
    
    @Override
    public void flowY(int width, int height, int preferred) {
        int y = 0;
        for (GuiChildControl child : controls) {
            child.setHeight(child.getPreferredHeight());
            child.setY(y);
            child.flowY();
            y += child.getHeight() + spacing;
        }
        cachedHeight = y;
    }
    
}
