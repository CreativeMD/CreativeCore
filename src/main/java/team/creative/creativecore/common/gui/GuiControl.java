package team.creative.creativecore.common.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.gui.event.GuiEvent;
import team.creative.creativecore.common.gui.event.GuiTooltipEvent;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.GuiStyle;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.text.TextBuilder;

public abstract class GuiControl {
    
    private IGuiParent parent;
    public final String name;
    public boolean enabled = true;
    
    private int x;
    private int y;
    private int width;
    private int height;
    
    public boolean visible = true;
    
    private List<Component> customTooltip;
    
    public GuiControl(String name, int x, int y, int width, int height) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    // BASICS
    
    public boolean isClient() {
        return parent.isClient();
    }
    
    public GuiControl setTooltip(List<Component> tooltip) {
        if (!tooltip.isEmpty())
            this.customTooltip = tooltip;
        return this;
    }
    
    public GuiControl setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    
    public void setParent(IGuiParent parent) {
        this.parent = parent;
    }
    
    public IGuiParent getParent() {
        return parent;
    }
    
    public int getContentWidth() {
        return width - getContentOffset() * 2;
    }
    
    public int getContentHeight() {
        return height - getContentOffset() * 2;
    }
    
    public int getControlOffsetX() {
        int offset = x;
        if (parent instanceof GuiControl)
            offset += ((GuiControl) parent).getControlOffsetX();
        return offset;
    }
    
    public int getControlOffsetY() {
        int offset = y;
        if (parent instanceof GuiControl)
            offset += ((GuiControl) parent).getControlOffsetY();
        return offset;
    }
    
    public String getNestedName() {
        if (getParent() instanceof GuiControl)
            return ((GuiControl) getParent()).getNestedName() + "." + name;
        return name;
    }
    
    public boolean hasLayer() {
        if (parent instanceof GuiControl)
            return ((GuiControl) parent).hasLayer();
        return false;
    }
    
    public GuiLayer getLayer() {
        if (parent instanceof GuiControl)
            return ((GuiControl) parent).getLayer();
        throw new RuntimeException("Invalid layer control");
    }
    
    public GuiStyle getStyle() {
        if (parent instanceof GuiControl)
            return ((GuiControl) parent).getStyle();
        throw new RuntimeException("Invalid layer control");
    }
    
    public abstract void init();
    
    public abstract void closed();
    
    public abstract void tick();
    
    public boolean is(String... name) {
        for (int i = 0; i < name.length; i++) {
            if (this.name.equalsIgnoreCase(name[i]))
                return true;
        }
        return false;
    }
    
    // SIZE
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public void initiateLayoutUpdate() {
        if (getParent() != null)
            getParent().initiateLayoutUpdate();
    }
    
    public void updateLayout() {}
    
    public abstract void setWidthLayout(int width);
    
    public abstract int getMinWidth();
    
    public abstract int getPreferredWidth();
    
    public int getMaxWidth() {
        return 0;
    }
    
    public abstract void setHeightLayout(int height);
    
    public abstract int getMinHeight();
    
    public abstract int getPreferredHeight();
    
    public int getMaxHeight() {
        return 0;
    }
    
    // INTERACTION
    
    public boolean testForDoubleClick(double x, double y) {
        return false;
    }
    
    public boolean isInteractable() {
        return enabled && visible;
    }
    
    public boolean isMouseOver(double x, double y) {
        return x >= this.x && x < this.x + this.width && y >= this.y && y < this.y + this.height;
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
    
    public void raiseEvent(GuiEvent event) {
        if (parent != null)
            parent.raiseEvent(event);
    }
    
    // APPERANCE
    
    public abstract ControlFormatting getControlFormatting();
    
    public int getContentOffset() {
        return getStyle().getContentOffset(getControlFormatting());
    }
    
    public GuiTooltipEvent getTooltipEvent(double x, double y) {
        List<Component> toolTip = getTooltip();
        
        if (customTooltip != null)
            if (toolTip == null)
                toolTip = customTooltip;
            else
                toolTip.addAll(customTooltip);
            
        if (toolTip == null) {
            String langTooltip = translateOrDefault(getNestedName() + ".tooltip", null);
            if (langTooltip != null)
                toolTip = new TextBuilder(langTooltip).build();
        }
        
        if (toolTip != null)
            return new GuiTooltipEvent(this, toolTip);
        return null;
    }
    
    public List<Component> getTooltip() {
        return null;
    }
    
    // RENDERING
    
    @OnlyIn(value = Dist.CLIENT)
    public boolean canOverlap() {
        return false;
    }
    
    @OnlyIn(value = Dist.CLIENT)
    public void render(PoseStack matrix, Rect controlRect, Rect realRect, int mouseX, int mouseY) {
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
        
        Rect rectCopy = null;
        if (!enabled)
            rectCopy = controlRect.copy();
        
        GuiStyle style = getStyle();
        ControlFormatting formatting = getControlFormatting();
        
        style.get(formatting.border).render(matrix, 0, 0, controlRect.getWidth(), controlRect.getHeight());
        
        int borderWidth = style.getBorder(formatting.border);
        controlRect.shrink(borderWidth);
        style.get(formatting.face, enabled && realRect.inside(mouseX, mouseY)).render(matrix, borderWidth, borderWidth, controlRect.getWidth(), controlRect.getHeight());
        
        renderContent(matrix, formatting, borderWidth, controlRect, realRect, mouseX, mouseY);
        
        if (!enabled) {
            realRect.scissor();
            style.disabled.render(matrix, realRect, rectCopy);
        }
    }
    
    @OnlyIn(value = Dist.CLIENT)
    protected void renderContent(PoseStack matrix, ControlFormatting formatting, int borderWidth, Rect controlRect, Rect realRect, int mouseX, int mouseY) {
        controlRect.shrink(formatting.padding);
        matrix.pushPose();
        matrix.translate(borderWidth + formatting.padding, borderWidth + formatting.padding, 0);
        renderContent(matrix, controlRect, controlRect.intersection(realRect), mouseX, mouseY);
        matrix.popPose();
    }
    
    @OnlyIn(value = Dist.CLIENT)
    protected void renderContent(PoseStack matrix, Rect controlRect, Rect realRect, int mouseX, int mouseY) {
        renderContent(matrix, controlRect, mouseX, mouseY);
    }
    
    @OnlyIn(value = Dist.CLIENT)
    protected abstract void renderContent(PoseStack matrix, Rect rect, int mouseX, int mouseY);
    
    // MINECRAFT
    
    public Player getPlayer() {
        return parent.getPlayer();
    }
    
    public boolean isClientSide() {
        return getPlayer().level.isClientSide;
    }
    
    // MANAGEMENT
    
    public void moveBehind(GuiControl reference) {
        parent.moveBehind(this, reference);
    }
    
    public void moveInFront(GuiControl reference) {
        parent.moveInFront(this, reference);
    }
    
    public void moveTop() {
        parent.moveTop(this);
    }
    
    public void moveBottom() {
        parent.moveBottom(this);
    }
    
    // UTILS
    
    @OnlyIn(value = Dist.CLIENT)
    public static String translate(String text, Object... parameters) {
        return I18n.get(text, parameters);
    }
    
    @OnlyIn(value = Dist.CLIENT)
    public static String translateOrDefault(String text, String defaultText) {
        if (I18n.exists(text))
            return translate(text);
        return defaultText;
    }
    
    @OnlyIn(value = Dist.CLIENT)
    public static void playSound(SoundInstance sound) {
        Minecraft.getInstance().getSoundManager().play(sound);
    }
    
    @OnlyIn(value = Dist.CLIENT)
    public static void playSound(SoundEvent event) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(event, 1.0F));
    }
    
    @OnlyIn(value = Dist.CLIENT)
    public static void playSound(SoundEvent event, float volume, float pitch) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(event, pitch, volume));
    }
}
