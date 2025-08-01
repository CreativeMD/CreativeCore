package team.creative.creativecore.client.gui;

import java.util.List;

import javax.annotation.Nullable;

import org.joml.Matrix3x2fStack;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiControlDistHandler;
import team.creative.creativecore.common.gui.event.GuiEvent;
import team.creative.creativecore.common.gui.event.GuiTooltipEvent;
import team.creative.creativecore.common.gui.flow.GuiSizeRule;
import team.creative.creativecore.common.gui.flow.GuiSizeRule.GuiFixedDimension;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.ControlFormattingCustom;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.gui.style.display.StyleDisplay;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.text.TextBuilder;

public abstract class GuiClientControl<T extends GuiControl> implements GuiControlDistHandler {
    
    public static Rect getScreenRect() {
        Minecraft mc = Minecraft.getInstance();
        return new Rect(0, 0, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
    }
    
    public static void scissor(Rect rect) {
        Window window = Minecraft.getInstance().getWindow();
        double realMinX = rect.minX * window.getGuiScale();
        double realMinY = window.getHeight() - (rect.minY + rect.getHeight()) * window.getGuiScale();
        double realMaxX = rect.getWidth() * window.getGuiScale();
        double realMaxY = rect.getHeight() * window.getGuiScale();
        
        RenderSystem.enableScissorForRenderTypeDraws((int) Math.floor(realMinX), (int) Math.floor(realMinY), (int) Math.ceil(realMaxX), (int) Math.ceil(realMaxY) + 1);
    }
    
    public final GuiControlRect rect = new GuiControlRect(this);
    public final T control;
    public boolean enabled = true;
    
    public boolean visible = true;
    
    public GuiSizeRule preferred;
    public boolean expandableX = false;
    public boolean expandableY = false;
    
    private List<Component> customTooltip;
    private ControlFormatting customFormatting;
    
    public GuiClientControl(T control) {
        this.control = control;
    }
    
    // BASICS
    
    @Nullable
    public GuiClientControl getParent() {
        if (control.getParent() instanceof GuiControl c)
            return (GuiClientControl) c.dist();
        return null;
    }
    
    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    @Override
    public void setFixed() {
        this.expandableX = false;
        this.expandableY = false;
    }
    
    @Override
    public void setFixedX() {
        this.expandableX = false;
    }
    
    @Override
    public void setFixedY() {
        this.expandableY = false;
    }
    
    @Override
    public void setExpandable() {
        this.expandableX = true;
        this.expandableY = true;
    }
    
    @Override
    public void setExpandableX() {
        this.expandableX = true;
    }
    
    @Override
    public void setExpandableY() {
        this.expandableY = true;
    }
    
    @Override
    public void setDim(int width, int height) {
        this.preferred = new GuiFixedDimension(width, height);
    }
    
    @Override
    public void setDim(GuiSizeRule dim) {
        this.preferred = dim;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public void removeFormatting() {
        customFormatting = null;
    }
    
    @Override
    public void setFormatting(ControlFormatting formatting) {
        customFormatting = formatting;
    }
    
    @Override
    public ControlFormattingCustom setCustomFormatting() {
        return (ControlFormattingCustom) (customFormatting = new ControlFormattingCustom(getControlFormatting()));
    }
    
    @Override
    public boolean isExpandableX() {
        return expandableX;
    }
    
    @Override
    public boolean isExpandableY() {
        return expandableY;
    }
    
    public GuiStyle getStyle() {
        if (control.getParent() instanceof GuiControl control)
            return ((GuiClientControl) control.dist()).getStyle();
        throw new RuntimeException("Invalid layer control");
    }
    
    public void raiseEvent(GuiEvent event) {
        control.raiseEvent(event);
    }
    
    // SIZE
    
    public Rect createChildRect(Rect contentRect, double scale, double xOffset, double yOffset) {
        return contentRect.child(rect, scale, xOffset, yOffset);
    }
    
    protected abstract void flowX(int width, int preferred);
    
    protected abstract void flowY(int width, int height, int preferred);
    
    protected int minWidth(int availableWidth) {
        return -1;
    }
    
    protected abstract int preferredWidth(int availableWidth);
    
    protected int maxWidth(int availableWidth) {
        return -1;
    }
    
    protected int minHeight(int width, int availableHeight) {
        return -1;
    }
    
    protected abstract int preferredHeight(int width, int availableHeight);
    
    protected int maxHeight(int width, int availableHeight) {
        return -1;
    }
    
    // INTERACTION
    
    public boolean testForDoubleClick(double x, double y, int button) {
        return false;
    }
    
    public boolean isInteractable() {
        return enabled && visible;
    }
    
    public void mouseMoved(double x, double y) {}
    
    public boolean mouseClicked(double x, double y, int button) {
        return false;
    }
    
    public boolean mouseDoubleClicked(double x, double y, int button) {
        return mouseClicked(x, y, button);
    }
    
    public void mouseReleased(double x, double y, int button) {}
    
    public void mouseDragged(double x, double y, int button, double dragX, double dragY, double time) {}
    
    public boolean mouseScrolled(double x, double y, double delta) {
        return false;
    }
    
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }
    
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }
    
    public boolean charTyped(char codePoint, int modifiers) {
        return false;
    }
    
    public void looseFocus() {}
    
    // APPERANCE
    
    protected abstract ControlFormatting defaultFormatting();
    
    public ControlFormatting getControlFormatting() {
        if (customFormatting != null)
            return customFormatting;
        return defaultFormatting();
    }
    
    public int getContentOffset() {
        return getStyle().getContentOffset(getControlFormatting());
    }
    
    // RENDERING
    
    public StyleDisplay getBorder(GuiStyle style, StyleDisplay display) {
        return display;
    }
    
    public StyleDisplay getBackground(GuiStyle style, StyleDisplay display) {
        return display;
    }
    
    public void render(GuiGraphics graphics, Rect controlRect, Rect realRect, double scale, int mouseX, int mouseY) {
        RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(Minecraft.getInstance().getMainRenderTarget().getDepthTexture(), 1.0);
        
        Rect rectCopy = null;
        if (!enabled)
            rectCopy = controlRect.copy();
        
        int width = rect.getWidth();
        int height = rect.getHeight();
        
        GuiStyle style = getStyle();
        ControlFormatting formatting = getControlFormatting();
        
        getBorder(style, style.get(formatting.border())).render(graphics, 0, 0, width, height);
        
        int borderSize = style.getBorder(formatting.border());
        
        width -= borderSize * 2;
        height -= borderSize * 2;
        
        getBackground(style, style.get(formatting.face(), enabled && realRect.inside(mouseX, mouseY))).render(graphics, borderSize, borderSize, width, height);
        
        controlRect.shrink(borderSize * scale);
        
        //graphics.flush();
        
        renderContent(graphics, formatting, borderSize, controlRect, realRect, scale, mouseX, mouseY);
        
        if (!enabled && formatting.hasDisabledEffect()) {
            scissor(realRect);
            //RenderSystem.disableDepthTest();
            // TODO 1.21.5 YET TO BE TESTED
            //RenderSystem.enableBlend();
            style.disabled.render(graphics, null, rectCopy);
            //RenderSystem.enableDepthTest();
        }
        
        //graphics.flush();
    }
    
    protected void renderContent(GuiGraphics graphics, ControlFormatting formatting, int borderWidth, Rect controlRect, Rect realRect, double scale, int mouseX, int mouseY) {
        Matrix3x2fStack pose = graphics.pose();
        controlRect.shrink(formatting.padding() * scale);
        if (!enabled)
            pose.pushMatrix();
        pose.translate(borderWidth + formatting.padding(), borderWidth + formatting.padding());
        renderContent(graphics, controlRect, controlRect.intersection(realRect), scale, mouseX, mouseY);
        if (!enabled)
            pose.popMatrix();
    }
    
    protected void renderContent(GuiGraphics graphics, Rect controlRect, Rect realRect, double scale, int mouseX, int mouseY) {
        renderContent(graphics, mouseX, mouseY);
    }
    
    protected abstract void renderContent(GuiGraphics graphics, int mouseX, int mouseY);
    
    // TOOLTIP
    
    public List<Component> getTooltip() {
        return customTooltip;
    }
    
    public GuiTooltipEvent getTooltipEvent(double x, double y) {
        List<Component> toolTip = getTooltip();
        
        if (customTooltip != null) {
            if (toolTip == null)
                toolTip = customTooltip;
            else if (toolTip != customTooltip)
                toolTip.addAll(customTooltip);
        }
        
        if (toolTip == null) {
            String langTooltip = GuiControl.translateOrDefault(control.getNestedName() + ".tooltip", null);
            if (langTooltip != null)
                toolTip = new TextBuilder(langTooltip).build();
        }
        
        if (toolTip != null)
            return new GuiTooltipEvent(control, toolTip);
        return null;
    }
    
    @Override
    public void setTooltip(List<Component> tooltip) {
        if (tooltip != null && tooltip.isEmpty())
            this.customTooltip = null;
        else
            this.customTooltip = tooltip;
    }
    
    @Override
    public void setTooltip(String translate) {
        setTooltip(new TextBuilder().translate(translate).build());
    }
    
    // SOUND
    
    public void playSound(SoundInstance sound) {
        Minecraft.getInstance().getSoundManager().play(sound);
    }
    
    @Override
    public void playSound(Holder.Reference<SoundEvent> sound) {
        playSound(sound.value());
    }
    
    @Override
    public void playSound(SoundEvent event) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(event, 1.0F));
    }
    
    @Override
    public void playSound(SoundEvent event, float volume, float pitch) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(event, pitch, volume));
    }
    
    @Override
    public void playSound(Holder.Reference<SoundEvent> event, float volume, float pitch) {
        playSound(event.value(), volume, pitch);
    }
}
