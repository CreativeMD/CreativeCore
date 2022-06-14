package team.creative.creativecore.common.gui.integration;

import com.mojang.blaze3d.Blaze3D;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
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
    
    public ScreenEventListener(IGuiIntegratedParent gui, Screen screen) {
        this.gui = gui;
        this.screen = screen;
    }
    
    public int getOffsetX() {
        return (screen.width - gui.getTopLayer().getWidth()) / 2;
    }
    
    public int getOffsetY() {
        return (screen.height - gui.getTopLayer().getHeight()) / 2;
    }
    
    public void tick() {
        if (doubleClickButton != -1 && Blaze3D.getTime() - time > DOUBLE_CLICK_TIME)
            fireRemaingEvents();
    }
    
    public double getEventTime() {
        return ((MouseHandlerAccessor) Minecraft.getInstance().mouseHandler).getLastMouseEventTime();
    }
    
    protected void fireRemaingEvents() {
        if (doubleClickButton != -1) {
            gui.getTopLayer().mouseClicked(null, x, y, doubleClickButton);
            if (released)
                gui.getTopLayer().mouseReleased(null, x, y, doubleClickButton);
            doubleClickButton = -1;
            released = false;
        }
    }
    
    @Override
    public void mouseMoved(double x, double y) {
        gui.getTopLayer().mouseMoved(null, x - getOffsetX(), y - getOffsetY());
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (gui.getTopLayer().testForDoubleClick(null, x - getOffsetX(), y - getOffsetY())) {
            
            if (doubleClickButton == button) {
                released = false;
                doubleClickButton = -1;
                return gui.getTopLayer().mouseDoubleClicked(null, x - getOffsetX(), y - getOffsetY(), button);
            }
            fireRemaingEvents();
            doubleClickButton = button;
            time = getEventTime();
            this.x = x - getOffsetX();
            this.y = y - getOffsetY();
            return true;
        }
        fireRemaingEvents();
        return gui.getTopLayer().mouseClicked(null, x - getOffsetX(), y - getOffsetY(), button);
    }
    
    @Override
    public boolean mouseReleased(double x, double y, int button) {
        if (doubleClickButton == button) {
            released = true;
            return true;
        }
        fireRemaingEvents();
        gui.getTopLayer().mouseReleased(null, x - getOffsetX(), y - getOffsetY(), button);
        return true;
    }
    
    @Override
    public boolean mouseDragged(double x, double y, int button, double dragX, double dragY) {
        if (doubleClickButton == -1)
            gui.getTopLayer().mouseDragged(null, x - getOffsetX(), y - getOffsetY(), button, dragX, dragY, getEventTime());
        return true;
    }
    
    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        return gui.getTopLayer().mouseScrolled(null, x - getOffsetX(), y - getOffsetY(), delta);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return gui.getTopLayer().keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return gui.getTopLayer().keyReleased(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return gui.getTopLayer().charTyped(codePoint, modifiers);
    }
    
    @Override
    public boolean changeFocus(boolean focus) {
        return false;
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
