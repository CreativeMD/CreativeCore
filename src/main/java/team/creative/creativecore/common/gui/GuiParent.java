package team.creative.creativecore.common.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.ArrayUtils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.gui.controls.layout.GuiLayoutControl;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.event.GuiControlClickEvent;
import team.creative.creativecore.common.gui.event.GuiEvent;
import team.creative.creativecore.common.gui.event.GuiEventManager;
import team.creative.creativecore.common.gui.event.GuiTooltipEvent;
import team.creative.creativecore.common.gui.sync.LayerOpenPacket;
import team.creative.creativecore.common.util.math.Rect;

public abstract class GuiParent extends GuiControl implements IGuiParent, Iterable<GuiControl> {
    
    private GuiEventManager eventManager;
    private List<GuiControl> controls = new ArrayList<>();
    
    @OnlyIn(value = Dist.CLIENT)
    protected int lastRenderedHeight;
    
    public GuiParent(String name, int x, int y, int width, int height) {
        super(name, x, y, width, height);
    }
    
    @Override
    public boolean isClient() {
        return getParent().isClient();
    }
    
    public float getScaleFactor() {
        return 1F;
    }
    
    public double getOffsetY() {
        return 0;
    }
    
    public double getOffsetX() {
        return 0;
    }
    
    @Override
    public Iterator<GuiControl> iterator() {
        return controls.iterator();
    }
    
    public GuiControl get(String name) {
        for (int i = 0; i < controls.size(); i++)
            if (controls.get(i).name.equalsIgnoreCase(name))
                return controls.get(i);
            else if (controls.get(i) instanceof GuiLayoutControl) {
                GuiControl result = ((GuiLayoutControl) controls.get(i)).get(name);
                if (result != null)
                    return result;
            }
        return null;
    }
    
    public boolean has(String name) {
        for (int i = 0; i < controls.size(); i++)
            if (controls.get(i).name.equalsIgnoreCase(name))
                return true;
        return false;
    }
    
    public void add(GuiControl control) {
        control.setParent(this);
        controls.add(control);
    }
    
    public void remove(GuiControl control) {
        controls.remove(control);
    }
    
    public void remove(String... include) {
        controls.removeIf((x) -> ArrayUtils.contains(include, x.name));
    }
    
    public void removeExclude(String... exclude) {
        controls.removeIf((x) -> !ArrayUtils.contains(exclude, x.name));
    }
    
    public boolean isEmpty() {
        return controls.isEmpty();
    }
    
    public void clear() {
        controls.clear();
    }
    
    public int size() {
        return controls.size();
    }
    
    @Override
    public void moveBehind(GuiControl toMove, GuiControl reference) {
        controls.remove(toMove);
        int index = controls.indexOf(reference);
        if (index != -1 && index < controls.size() - 1)
            controls.add(index + 1, toMove);
        else
            moveBottom(toMove);
    }
    
    @Override
    public void moveInFront(GuiControl toMove, GuiControl reference) {
        controls.remove(toMove);
        int index = controls.indexOf(reference);
        if (index != -1)
            controls.add(index, toMove);
        else
            moveTop(toMove);
    }
    
    @Override
    public void moveTop(GuiControl toMove) {
        controls.remove(toMove);
        controls.add(0, toMove);
    }
    
    @Override
    public void moveBottom(GuiControl toMove) {
        controls.remove(toMove);
        controls.add(toMove);
    }
    
    @Override
    @OnlyIn(value = Dist.CLIENT)
    protected void renderContent(MatrixStack matrix, Rect contentRect, Rect realContentRect, int mouseX, int mouseY) {
        float scale = getScaleFactor();
        double xOffset = getOffsetX();
        double yOffset = getOffsetY();
        
        lastRenderedHeight = 0;
        
        for (int i = controls.size() - 1; i >= 0; i--) {
            GuiControl control = controls.get(i);
            
            if (!control.visible)
                continue;
            
            Rect controlRect = contentRect.child((control.getX() + xOffset) * scale, (control.getY() + yOffset) * scale, control.getWidth() * scale, control.getHeight() * scale);
            Rect realRect = realContentRect.intersection(controlRect);
            if (realRect != null || control.canOverlap()) {
                if (control.canOverlap())
                    RenderSystem.disableScissor();
                else
                    realRect.scissor();
                
                matrix.pushPose();
                matrix.translate((control.getX() + xOffset) * scale, (control.getY() + yOffset) * scale, 10);
                control.render(matrix, controlRect, realRect, mouseX, mouseY);
                matrix.popPose();
            }
            
            lastRenderedHeight = (int) Math.max(lastRenderedHeight, (control.getY() + control.getHeight()) * scale);
            
        }
        super.renderContent(matrix, contentRect, realContentRect, mouseX, mouseY);
    }
    
    @Override
    @OnlyIn(value = Dist.CLIENT)
    protected void renderContent(MatrixStack matrix, Rect rect, int mouseX, int mouseY) {}
    
    @Override
    public boolean isContainer() {
        return getParent().isContainer();
    }
    
    @Override
    public void init() {
        for (int i = 0; i < controls.size(); i++)
            controls.get(i).init();
    }
    
    @Override
    public void closed() {
        for (int i = 0; i < controls.size(); i++)
            controls.get(i).closed();
    }
    
    @Override
    public void tick() {
        for (int i = 0; i < controls.size(); i++)
            controls.get(i).tick();
    }
    
    @Override
    public void closeTopLayer() {
        getParent().closeTopLayer();
    }
    
    @Override
    public GuiLayer openLayer(LayerOpenPacket packet) {
        return getParent().openLayer(packet);
    }
    
    @Override
    public GuiTooltipEvent getTooltipEvent(double x, double y) {
        GuiTooltipEvent event = super.getTooltipEvent(x, y);
        if (event != null)
            return event;
        
        x *= getScaleFactor();
        y *= getScaleFactor();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        for (int i = 0; i < controls.size(); i++) {
            GuiControl control = controls.get(i);
            if (control.isInteractable() && control.isMouseOver(x, y)) {
                event = control.getTooltipEvent(x - control.getX(), y - control.getY());
                if (event != null)
                    return event;
            }
        }
        return null;
    }
    
    @Override
    public boolean testForDoubleClick(double x, double y) {
        x *= getScaleFactor();
        y *= getScaleFactor();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        for (int i = 0; i < controls.size(); i++) {
            GuiControl control = controls.get(i);
            if (control.isInteractable() && control.testForDoubleClick(x, y))
                return true;
        }
        return false;
    }
    
    @Override
    public void mouseMoved(double x, double y) {
        x *= getScaleFactor();
        y *= getScaleFactor();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        for (int i = 0; i < controls.size(); i++) {
            GuiControl control = controls.get(i);
            if (control.isInteractable())
                control.mouseMoved(x - control.getX(), y - control.getY());
        }
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        x *= getScaleFactor();
        y *= getScaleFactor();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        boolean result = false;
        for (int i = 0; i < controls.size(); i++) {
            GuiControl control = controls.get(i);
            if (!result && control.isInteractable() && control.isMouseOver(x, y) && control.mouseClicked(x - control.getX(), y - control.getY(), button)) {
                raiseEvent(new GuiControlClickEvent(control, button, false));
                result = true;
            } else
                control.looseFocus();
        }
        return result;
    }
    
    @Override
    public boolean mouseDoubleClicked(double x, double y, int button) {
        x *= getScaleFactor();
        y *= getScaleFactor();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        boolean result = false;
        for (int i = 0; i < controls.size(); i++) {
            GuiControl control = controls.get(i);
            if (!result && control.isInteractable() && control.isMouseOver(x, y) && control.mouseDoubleClicked(x - control.getX(), y - control.getY(), button)) {
                raiseEvent(new GuiControlClickEvent(control, button, false));
                result = true;
            } else
                control.looseFocus();
        }
        return result;
    }
    
    @Override
    public void mouseReleased(double x, double y, int button) {
        x *= getScaleFactor();
        y *= getScaleFactor();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        for (int i = 0; i < controls.size(); i++) {
            GuiControl control = controls.get(i);
            if (control.isInteractable())
                control.mouseReleased(x - control.getX(), y - control.getY(), button);
        }
    }
    
    @Override
    public void mouseDragged(double x, double y, int button, double dragX, double dragY, double time) {
        x *= getScaleFactor();
        y *= getScaleFactor();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        for (int i = 0; i < controls.size(); i++) {
            GuiControl control = controls.get(i);
            if (control.isInteractable())
                control.mouseDragged(x - control.getX(), y - control.getY(), button, dragX, dragY, time);
        }
    }
    
    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        x *= getScaleFactor();
        y *= getScaleFactor();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        for (int i = 0; i < controls.size(); i++) {
            GuiControl control = controls.get(i);
            if (control.isInteractable() && control.isMouseOver(x, y) && control.mouseScrolled(x - control.getX(), y - control.getY(), delta))
                return true;
        }
        return false;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (int i = 0; i < controls.size(); i++) {
            GuiControl control = controls.get(i);
            if (control.isInteractable() && control.keyPressed(keyCode, scanCode, modifiers))
                return true;
        }
        return false;
    }
    
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (int i = 0; i < controls.size(); i++) {
            GuiControl control = controls.get(i);
            if (control.isInteractable() && control.keyReleased(keyCode, scanCode, modifiers))
                return true;
        }
        return false;
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (int i = 0; i < controls.size(); i++) {
            GuiControl control = controls.get(i);
            if (control.isInteractable() && control.charTyped(codePoint, modifiers))
                return true;
        }
        return false;
    }
    
    @Override
    public void looseFocus() {
        for (int i = 0; i < controls.size(); i++)
            controls.get(i).looseFocus();
    }
    
    @Override
    public void raiseEvent(GuiEvent event) {
        if (getParent() == null)
            return;
        if (eventManager != null)
            eventManager.raiseEvent(event);
        if (!event.isCanceled())
            getParent().raiseEvent(event);
    }
    
    public void registerEventClick(Consumer<GuiControlClickEvent> consumer) {
        registerEvent(GuiControlClickEvent.class, consumer);
    }
    
    public void registerEventChanged(Consumer<GuiControlChangedEvent> consumer) {
        registerEvent(GuiControlChangedEvent.class, consumer);
    }
    
    public <T extends GuiEvent> void registerEvent(Class<T> clazz, Consumer<T> action) {
        if (eventManager == null)
            eventManager = new GuiEventManager();
        eventManager.registerEvent(clazz, action);
    }
    
    @Override
    public void updateLayout() {
        super.updateLayout();
        for (int i = 0; i < controls.size(); i++)
            controls.get(i).updateLayout();
    }
    
}
