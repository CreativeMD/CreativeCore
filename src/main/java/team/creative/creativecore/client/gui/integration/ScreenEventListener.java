package team.creative.creativecore.client.gui.integration;

import com.mojang.blaze3d.Blaze3D;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import team.creative.creativecore.client.gui.GuiClientLayer;
import team.creative.creativecore.common.gui.integration.IGuiIntegratedParent;
import team.creative.creativecore.mixin.MouseHandlerAccessor;

public class ScreenEventListener implements GuiEventListener, NarratableEntry {
    
    public static final double DOUBLE_CLICK_TIME = 0.2;
    
    private final IGuiIntegratedParent gui;
    private final Screen screen;
    private MouseButtonInfo doubleClickButton = null;
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
        if (doubleClickButton != null && Blaze3D.getTime() - time > DOUBLE_CLICK_TIME)
            fireRemaingEvents();
    }
    
    public double getEventTime() {
        return ((MouseHandlerAccessor) Minecraft.getInstance().mouseHandler).getLastHandleMovementTime();
    }
    
    protected void fireRemaingEvents() {
        if (doubleClickButton != null) {
            getTopLayer().mouseClicked(x, y, doubleClickButton);
            if (released)
                getTopLayer().mouseReleased(x, y, doubleClickButton);
            doubleClickButton = null;
            released = false;
        }
    }
    
    @Override
    public void mouseMoved(double x, double y) {
        getTopLayer().mouseMoved(x - getOffsetX(), y - getOffsetY());
    }
    
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (getTopLayer().testForDoubleClick(event.x() - getOffsetX(), event.y() - getOffsetY(), event.buttonInfo())) {
            
            if (doubleClickButton.equals(event)) {
                released = false;
                doubleClickButton = null;
                return getTopLayer().mouseDoubleClicked(event.x() - getOffsetX(), event.y() - getOffsetY(), event.buttonInfo());
            }
            fireRemaingEvents();
            doubleClickButton = event.buttonInfo();
            time = getEventTime();
            this.x = x - getOffsetX();
            this.y = y - getOffsetY();
            return true;
        }
        fireRemaingEvents();
        return getTopLayer().mouseClicked(event.x() - getOffsetX(), event.y() - getOffsetY(), event.buttonInfo());
    }
    
    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (doubleClickButton == event.buttonInfo()) {
            released = true;
            return true;
        }
        fireRemaingEvents();
        getTopLayer().mouseReleased(event.x() - getOffsetX(), event.y() - getOffsetY(), event.buttonInfo());
        return true;
    }
    
    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (doubleClickButton == null)
            getTopLayer().mouseDragged(event.x() - getOffsetX(), event.y() - getOffsetY(), event.buttonInfo(), dragX, dragY, Blaze3D.getTime() - time);
        return true;
    }
    
    @Override
    public boolean mouseScrolled(double x, double y, double deltaX, double deltaY) {
        return getTopLayer().mouseScrolled(x - getOffsetX(), y - getOffsetY(), deltaY);
    }
    
    @Override
    public boolean keyPressed(KeyEvent key) {
        return getTopLayer().keyPressed(key);
    }
    
    @Override
    public boolean keyReleased(KeyEvent key) {
        return getTopLayer().keyReleased(key);
    }
    
    @Override
    public boolean charTyped(CharacterEvent event) {
        return getTopLayer().charTyped(event);
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
