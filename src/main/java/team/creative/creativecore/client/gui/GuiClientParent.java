package team.creative.creativecore.client.gui;

import java.util.ListIterator;

import org.joml.Matrix3x2fStack;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiControls;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.GuiParent.GuiParentDistHandler;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.event.GuiControlClickEvent;
import team.creative.creativecore.common.gui.event.GuiTooltipEvent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiClientParent<T extends GuiParent> extends GuiClientControl<T> implements GuiParentDistHandler {
    
    public GuiFlow flow;
    public Align align = Align.LEFT;
    public VAlign valign = VAlign.TOP;
    public int spacing = 2;
    
    private double scale = 1;
    private double scaleInv = 1;
    
    private GuiControls controls;
    
    public GuiClientParent(T control) {
        super(control);
    }
    
    @Override
    public void initControlList(GuiControls controls) {
        this.controls = controls;
    }
    
    @Override
    public Iterable<GuiClientControl> controls() {
        return (Iterable<GuiClientControl>) (Object) controls.distControls();
    }
    
    @Override
    public Iterable<GuiClientControl> hoverControls() {
        return (Iterable<GuiClientControl>) (Object) controls.distHoverControls();
    }
    
    @Override
    public Iterable<GuiClientControl> all() {
        return (Iterable<GuiClientControl>) (Object) controls.distAll();
    }
    
    @Override
    public void setScale(double scale) {
        this.scale = scale;
        this.scaleInv = 1 / scale;
    }
    
    public final double scaleFactor() {
        return scale;
    }
    
    public final double scaleFactorInv() {
        return scaleInv;
    }
    
    public double getOffsetY() {
        return 0;
    }
    
    public double getOffsetX() {
        return 0;
    }
    
    @Override
    public void setAlign(Align align) {
        this.align = align;
    }
    
    @Override
    public void setVAlign(VAlign valign) {
        this.valign = valign;
    }
    
    @Override
    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }
    
    @Override
    public void setFlow(GuiFlow flow) {
        this.flow = flow;
    }
    
    protected void renderControls(GuiGraphics graphics, Rect contentRect, Rect realContentRect, int mouseX, int mouseY, ListIterator<GuiClientControl> collection, double scale,
            double xOffset, double yOffset, boolean hover) {
        Matrix3x2fStack pose = graphics.pose();
        
        while (collection.hasPrevious()) {
            GuiClientControl control = collection.previous();
            
            if (!control.visible)
                continue;
            
            Rect controlContentRect = control.createChildRect(contentRect, scale, xOffset, yOffset);
            Rect realRect = realContentRect.intersection(controlContentRect);
            if (realRect != null || hover) {
                if (hover)
                    RenderSystem.disableScissorForRenderTypeDraws();
                else
                    scissor(realRect);
                
                pose.pushMatrix();
                pose.translate((float) (control.rect.getX() + xOffset), (float) (control.rect.getY() + yOffset));
                renderControl(graphics, control, controlContentRect, realRect, scale, mouseX, mouseY, hover);
                pose.popMatrix();
            }
        }
    }
    
    protected void renderControl(GuiGraphics graphics, GuiClientControl control, Rect controlContentRect, Rect realRect, double scale, int mouseX, int mouseY, boolean hover) {
        control.render(graphics, controlContentRect, hover ? controlContentRect : realRect, scale, mouseX, mouseY);
    }
    
    @Override
    protected void renderContent(GuiGraphics graphics, Rect contentRect, Rect realContentRect, double scale, int mouseX, int mouseY) {
        if (realContentRect == null)
            return;
        
        Matrix3x2fStack pose = graphics.pose();
        float controlScale = (float) scaleFactor();
        scale *= scaleFactor();
        double xOffset = getOffsetX();
        double yOffset = getOffsetY();
        
        pose.scale(controlScale, controlScale);
        
        renderControls(graphics, contentRect, realContentRect, mouseX, mouseY, (ListIterator<GuiClientControl>) (Object) controls.distListControls(controls.controlSize()), scale,
            xOffset, yOffset, false);
        renderControls(graphics, contentRect, realContentRect, mouseX, mouseY, (ListIterator<GuiClientControl>) (Object) controls.distHoverListControls(controls.hoverSize()),
            scale, xOffset, yOffset, true);
        
        super.renderContent(graphics, contentRect, realContentRect, scale, mouseX, mouseY);
    }
    
    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY) {}
    
    public boolean isMouseOverHovered(double x, double y) {
        x *= scaleFactorInv();
        y *= scaleFactorInv();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        for (GuiClientControl control : hoverControls())
            if (control.rect.inside(x, y))
                return true;
        return false;
    }
    
    @Override
    public GuiTooltipEvent getTooltipEvent(double x, double y) {
        GuiTooltipEvent event = super.getTooltipEvent(x, y);
        if (event != null)
            return event;
        
        x *= scaleFactorInv();
        y *= scaleFactorInv();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        for (GuiClientControl control : all())
            if (control.isInteractable() && control.rect.inside(x, y)) {
                event = control.getTooltipEvent(x - control.rect.getX(), y - control.rect.getY());
                if (event != null)
                    return event;
            }
        return null;
    }
    
    @Override
    public boolean testForDoubleClick(double x, double y, int button) {
        x *= scaleFactorInv();
        y *= scaleFactorInv();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        for (GuiClientControl control : all())
            if (control.isInteractable() && control.rect.inside(x, y) && control.testForDoubleClick(x - control.rect.getX(), y - control.rect.getY(), button))
                return true;
        return false;
        
    }
    
    @Override
    public void mouseMoved(double x, double y) {
        x *= scaleFactorInv();
        y *= scaleFactorInv();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        for (GuiClientControl control : all())
            if (control.isInteractable())
                control.mouseMoved(x - control.rect.getX(), y - control.rect.getY());
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        x *= scaleFactorInv();
        y *= scaleFactorInv();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        boolean result = false;
        for (GuiClientControl control : all())
            if (!result && control.isInteractable() && control.rect.inside(x, y) && control.mouseClicked(x - control.rect.getX(), y - control.rect.getY(), button)) {
                raiseEvent(new GuiControlClickEvent(control.control, button, false));
                result = true;
            } else
                control.looseFocus();
        return result;
    }
    
    @Override
    public boolean mouseDoubleClicked(double x, double y, int button) {
        x *= scaleFactorInv();
        y *= scaleFactorInv();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        boolean result = false;
        for (GuiClientControl control : all())
            if (!result && control.isInteractable() && control.rect.inside(x, y) && control.mouseDoubleClicked(x - control.rect.getX(), y - control.rect.getY(), button)) {
                raiseEvent(new GuiControlClickEvent(control.control, button, false));
                result = true;
            } else
                control.looseFocus();
        return result;
        
    }
    
    @Override
    public void mouseReleased(double x, double y, int button) {
        x *= scaleFactorInv();
        y *= scaleFactorInv();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        for (GuiClientControl control : all())
            if (control.isInteractable())
                control.mouseReleased(x - control.rect.getX(), y - control.rect.getY(), button);
    }
    
    @Override
    public void mouseDragged(double x, double y, int button, double dragX, double dragY, double time) {
        x *= scaleFactorInv();
        y *= scaleFactorInv();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        for (GuiClientControl control : all())
            if (control.isInteractable())
                control.mouseDragged(x - control.rect.getX(), y - control.rect.getY(), button, dragX, dragY, time);
    }
    
    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        x *= scaleFactorInv();
        y *= scaleFactorInv();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        for (GuiClientControl control : all())
            if (control.isInteractable() && control.rect.inside(x, y) && control.mouseScrolled(x - control.rect.getX(), y - control.rect.getY(), delta))
                return true;
        return false;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (GuiClientControl control : all())
            if (control.isInteractable() && control.keyPressed(keyCode, scanCode, modifiers))
                return true;
        return false;
    }
    
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (GuiClientControl control : all())
            if (control.isInteractable() && control.keyReleased(keyCode, scanCode, modifiers))
                return true;
        return false;
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (GuiClientControl control : all())
            if (control.isInteractable() && control.charTyped(codePoint, modifiers))
                return true;
        return false;
    }
    
    @Override
    public void looseFocus() {
        for (GuiClientControl control : all())
            control.looseFocus();
    }
    
    @Override
    public void flowX(int width, int preferred) {
        flow.flowX(controls.streamDistControls().map(x -> ((GuiClientControl) x).rect).toList(), spacing, align, width, preferred, endlessX());
    }
    
    @Override
    public void flowY(int width, int height, int preferred) {
        flow.flowY(controls.streamDistControls().map(x -> ((GuiClientControl) x).rect).toList(), spacing, valign, width, height, preferred, endlessY());
    }
    
    protected boolean endlessX() {
        return false;
    }
    
    protected boolean endlessY() {
        return false;
    }
    
    @Override
    protected int minWidth(int availableWidth) {
        return Mth.ceil(flow.minWidth(controls.streamDistControls().map(x -> ((GuiClientControl) x).rect).toList(), spacing, availableWidth) * scale);
    }
    
    @Override
    protected int preferredWidth(int availableWidth) {
        return Mth.ceil(flow.preferredWidth(controls.streamDistControls().map(x -> ((GuiClientControl) x).rect).toList(), spacing, availableWidth) * scale);
    }
    
    @Override
    protected int minHeight(int width, int availableHeight) {
        return Mth.ceil(flow.minHeight(controls.streamDistControls().map(x -> ((GuiClientControl) x).rect).toList(), spacing, width, availableHeight) * scale);
    }
    
    @Override
    protected int preferredHeight(int width, int availableHeight) {
        return Mth.ceil(flow.preferredHeight(controls.streamDistControls().map(x -> ((GuiClientControl) x).rect).toList(), spacing, width, availableHeight) * scale);
    }
    
    @Override
    protected ControlFormatting defaultFormatting() {
        return ControlFormatting.TRANSPARENT;
    }
    
    @Override
    public void applyOffset(GuiControl control, Rect rect) {
        rect.move(((GuiClientControl) control.dist()).rect.getX() + getOffsetX() + getContentOffset(), ((GuiClientControl) control.dist()).rect
                .getY() + getOffsetY() + getContentOffset());
        rect.scale(scaleFactor());
    }
    
}
