package team.creative.creativecore.common.gui.controls.parent;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.flow.GuiSizeRule;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleFace;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.gui.style.display.StyleDisplay;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.math.vec.SmoothValue;

public class GuiScrollX extends GuiParent {
    
    public int maxScroll = 0;
    public SmoothValue scrolled = new SmoothValue(200);
    public boolean dragged;
    public int scrollbarHeight = 3;
    public boolean hoveredScroll;
    protected int cachedWidth;
    
    public GuiScrollX() {
        this("");
    }
    
    public GuiScrollX(String name) {
        super(name, GuiFlow.STACK_X);
    }
    
    public GuiScrollX setHovered() {
        this.hoveredScroll = true;
        return this;
    }
    
    public GuiScrollX setHover(boolean hover) {
        this.hoveredScroll = hover;
        return this;
    }
    
    @Override
    public GuiScrollX setDim(int width, int height) {
        return (GuiScrollX) super.setDim(width, height);
    }
    
    @Override
    public GuiScrollX setDim(GuiSizeRule dim) {
        return (GuiScrollX) super.setDim(dim);
    }
    
    @Override
    public GuiScrollX setExpandable() {
        return (GuiScrollX) super.setExpandable();
    }
    
    @Override
    public double getOffsetX() {
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
        scroll(scrolled);
        return true;
    }
    
    public void scroll(double scrolled) {
        this.scrolled.set(this.scrolled.aimed() - scrolled * 10);
        onScrolled();
    }
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        if (button == 0 && rect.getHeight() - y <= scrollbarHeight && needsScrollbar(rect)) {
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
            int completeWidth = (int) (rect.getWidth() - style.getBorder(formatting.border) * 2);
            
            int scrollThingWidth = Math.max(10, Math.min(completeWidth, (int) ((float) completeWidth / cachedWidth * completeWidth)));
            if (cachedWidth < completeWidth)
                scrollThingWidth = completeWidth;
            
            double percent = (x) / (completeWidth - scrollThingWidth);
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
        return cachedWidth > rect.getWidth() - getContentOffset() * 2;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderContent(PoseStack pose, GuiChildControl control, ControlFormatting formatting, int borderWidth, Rect controlRect, Rect realRect, double scale, int mouseX, int mouseY) {
        pose.pushPose();
        super.renderContent(pose, control, formatting, borderWidth, controlRect, realRect, scale, mouseX, mouseY);
        pose.popPose();
        
        if (!needsScrollbar(controlRect) && hoveredScroll)
            return;
        
        if (hoveredScroll)
            RenderSystem.disableDepthTest();
        
        float controlInvScale = (float) scaleFactorInv();
        pose.scale(controlInvScale, controlInvScale, controlInvScale);
        
        realRect.scissor();
        GuiStyle style = getStyle();
        
        scrolled.tick();
        
        int completeWidth = control.getWidth() - borderWidth * 2;
        
        int scrollThingWidth = Math.max(10, Math.min(completeWidth, (int) ((float) completeWidth / cachedWidth * completeWidth)));
        if (scrollThingWidth < completeWidth)
            scrollThingWidth = completeWidth;
        double percent = scrolled.current() / maxScroll;
        
        StyleDisplay display = hoveredScroll ? style.disabled : style.get(ControlStyleFace.CLICKABLE, false);
        display.render(pose, control.getWidth() - scrollbarHeight - borderWidth, (int) (percent * (completeWidth - scrollThingWidth)) + borderWidth, scrollbarHeight,
            scrollThingWidth);
        
        maxScroll = Math.max(0, (cachedWidth - completeWidth) + formatting.padding * 2 + 1);
        
        float controlScale = (float) scaleFactor();
        pose.scale(controlScale, controlScale, controlScale);
        
        if (hoveredScroll)
            RenderSystem.enableDepthTest();
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
        cachedWidth = preferred;
    }
    
    @Override
    public void flowY(int width, int height, int preferred) {
        if (!hoveredScroll)
            height -= scrollbarHeight;
        super.flowY(width, height, preferred);
    }
    
}
