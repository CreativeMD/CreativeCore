package team.creative.creativecore.client.gui.integration;

import com.mojang.blaze3d.Blaze3D;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import team.creative.creativecore.client.gui.GuiClientLayer;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;
import team.creative.creativecore.mixin.MouseHandlerAccessor;

public class ScreenEventListener implements GuiEventListener, NarratableEntry {
    
    public static final double DOUBLE_CLICK_TIME = 0.2;
    
    private final IGuiIntegratedParent gui;
    private final Screen screen;
    private int doubleClickButton = -1;
    private double time;
    private double x;
    private double y;
    private boolean released = false;
    private boolean focused;
    
    public ScreenEventListener(IGuiIntegratedParent gui, Screen screen) {
        this.gui = gui;
        this.screen = screen;
    }
    
    public GuiClientLayer getTopLayer() {
        return (GuiClientLayer) gui.getTopLayer().dist();
    }
    
    public int getOffsetX() {
        return (screen.width - getTopLayer().getWidth()) / 2;
    }
    
    public int getOffsetY() {
        return (screen.height - getTopLayer().getHeight()) / 2;
    }
    
    public void tick() {
        if (doubleClickButton != -1 && Blaze3D.getTime() - time > DOUBLE_CLICK_TIME)
            fireRemaingEvents();
    }
    
    public double getEventTime() {
        return ((MouseHandlerAccessor) Minecraft.getInstance().mouseHandler).getLastHandleMovementTime();
    }
    
    protected void fireRemaingEvents() {
        if (doubleClickButton != -1) {
            getTopLayer().mouseClicked(x, y, doubleClickButton);
            if (released)
                getTopLayer().mouseReleased(x, y, doubleClickButton);
            doubleClickButton = -1;
            released = false;
        }
    }
    
    @Override
    public void mouseMoved(double x, double y) {
        getTopLayer().mouseMoved(x - getOffsetX(), y - getOffsetY());
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (getTopLayer().testForDoubleClick(x - getOffsetX(), y - getOffsetY(), button)) {
            
            if (doubleClickButton == button) {
                released = false;
                doubleClickButton = -1;
                return getTopLayer().mouseDoubleClicked(x - getOffsetX(), y - getOffsetY(), button);
            }
            fireRemaingEvents();
            doubleClickButton = button;
            time = getEventTime();
            this.x = x - getOffsetX();
            this.y = y - getOffsetY();
            return true;
        }
        fireRemaingEvents();
        return getTopLayer().mouseClicked(x - getOffsetX(), y - getOffsetY(), button);
    }
    
    @Override
    public boolean mouseReleased(double x, double y, int button) {
        if (doubleClickButton == button) {
            released = true;
            return true;
        }
        fireRemaingEvents();
        getTopLayer().mouseReleased(x - getOffsetX(), y - getOffsetY(), button);
        return true;
    }
    
    @Override
    public boolean mouseDragged(double x, double y, int button, double dragX, double dragY) {
        if (doubleClickButton == -1)
            getTopLayer().mouseDragged(x - getOffsetX(), y - getOffsetY(), button, dragX, dragY, Blaze3D.getTime() - time);
        return true;
    }
    
    @Override
    public boolean mouseScrolled(double x, double y, double deltaX, double deltaY) {
        return getTopLayer().mouseScrolled(x - getOffsetX(), y - getOffsetY(), deltaY);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return getTopLayer().keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return getTopLayer().keyReleased(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return getTopLayer().charTyped(codePoint, modifiers);
    }
    
    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }
    
    @Override
    public boolean isFocused() {
        return focused;
    }
    
    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return true;
    }
    
    @Override
    public void updateNarration(NarrationElementOutput p_169152_) {}
    
    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }
    
}
