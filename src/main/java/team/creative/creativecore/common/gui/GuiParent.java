package team.creative.creativecore.common.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.ArrayUtils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.event.GuiControlClickEvent;
import team.creative.creativecore.common.gui.event.GuiEvent;
import team.creative.creativecore.common.gui.event.GuiEventManager;
import team.creative.creativecore.common.gui.event.GuiTooltipEvent;
import team.creative.creativecore.common.gui.sync.LayerOpenPacket;
import team.creative.creativecore.common.util.math.geo.Rect;

public abstract class GuiParent extends GuiControl implements IGuiParent, Iterable<GuiChildControl> {
    
    private GuiEventManager eventManager;
    protected List<GuiChildControl> controls = new ArrayList<>();
    protected List<GuiChildControl> hoverControls = new ArrayList<>();
    
    protected Align align = Align.LEFT;
    protected VAlign valign = VAlign.TOP;
    protected int spacing = 2;
    
    public GuiParent(String name) {
        super(name);
    }
    
    public GuiParent(String name, int width, int height) {
        super(name, width, height);
    }
    
    @Override
    public boolean isClient() {
        return getParent().isClient();
    }
    
    public double getScaleFactor() {
        return 1;
    }
    
    public double getOffsetY() {
        return 0;
    }
    
    public double getOffsetX() {
        return 0;
    }
    
    @Override
    public boolean isExpandable() {
        if (super.isExpandable())
            return true;
        for (GuiChildControl child : controls)
            if (child.control.isExpandable())
                return true;
        return false;
    }
    
    private static GuiControl get(String name, List<GuiChildControl> collection) {
        for (int i = 0; i < collection.size(); i++) {
            GuiControl control = collection.get(i).control;
            if (control.name.equalsIgnoreCase(name))
                return control;
            else if (control instanceof GuiParent) {
                if (control.name.isBlank()) {
                    GuiControl result = ((GuiParent) control).get(name);
                    if (result != null)
                        return result;
                } else if (name.startsWith(control.name + "."))
                    return ((GuiParent) control).get(name.substring(control.name.length() + 1));
            }
        }
        return null;
    }
    
    public GuiControl get(String name) {
        GuiControl result = get(name, controls);
        if (result != null)
            return result;
        return get(name, hoverControls);
    }
    
    public boolean has(String name) {
        return get(name) != null;
    }
    
    public void add(GuiControl control) {
        control.setParent(this);
        controls.add(new GuiChildControl(control));
    }
    
    public void addHover(GuiControl control) {
        control.setParent(this);
        hoverControls.add(new GuiChildControl(control));
    }
    
    public GuiChildControl remove(GuiControl control) {
        for (GuiChildControl child : controls)
            if (child.control == control)
                return child;
        for (GuiChildControl child : hoverControls)
            if (child.control == control)
                return child;
        return null;
    }
    
    public void remove(String... include) {
        controls.removeIf((x) -> ArrayUtils.contains(include, x.control.name));
        hoverControls.removeIf((x) -> ArrayUtils.contains(include, x.control.name));
    }
    
    public void removeExclude(String... exclude) {
        controls.removeIf((x) -> !ArrayUtils.contains(exclude, x.control.name));
        hoverControls.removeIf((x) -> !ArrayUtils.contains(exclude, x.control.name));
    }
    
    public boolean isEmpty() {
        return controls.isEmpty() && hoverControls.isEmpty();
    }
    
    public void clear() {
        controls.clear();
        hoverControls.clear();
    }
    
    public int size() {
        return controls.size() + hoverControls.size();
    }
    
    @Override
    public Iterator<GuiChildControl> iterator() {
        return new Iterator<GuiChildControl>() {
            
            Iterator<GuiChildControl> itr = controls.iterator();
            boolean first = true;
            
            @Override
            public boolean hasNext() {
                if (itr.hasNext())
                    return true;
                if (first) {
                    itr = hoverControls.iterator();
                    first = false;
                }
                return itr.hasNext();
            }
            
            @Override
            public GuiChildControl next() {
                return itr.next();
            }
        };
    }
    
    protected void renderContent(PoseStack matrix, Rect contentRect, Rect realContentRect, int mouseX, int mouseY, List<GuiChildControl> collection, double scale, double xOffset, double yOffset, boolean hover) {
        for (int i = collection.size() - 1; i >= 0; i--) {
            GuiChildControl child = collection.get(i);
            GuiControl control = child.control;
            
            if (!control.visible)
                continue;
            
            Rect controlRect = contentRect.child(child.rect, scale, xOffset, yOffset);
            Rect realRect = realContentRect.intersection(controlRect);
            if (realRect != null || hover) {
                if (hover)
                    RenderSystem.disableScissor();
                else
                    realRect.scissor();
                
                matrix.pushPose();
                matrix.translate((child.getX() + xOffset) * scale, (child.getY() + yOffset) * scale, 10);
                control.render(matrix, child, controlRect, realRect, mouseX, mouseY);
                matrix.popPose();
            }
        }
    }
    
    @Override
    @OnlyIn(value = Dist.CLIENT)
    protected void renderContent(PoseStack matrix, GuiChildControl control, Rect contentRect, Rect realContentRect, int mouseX, int mouseY) {
        if (realContentRect == null)
            return;
        double scale = getScaleFactor();
        double xOffset = getOffsetX();
        double yOffset = getOffsetY();
        
        renderContent(matrix, contentRect, realContentRect, mouseX, mouseY, controls, scale, xOffset, yOffset, false);
        renderContent(matrix, contentRect, realContentRect, mouseX, mouseY, hoverControls, scale, xOffset, yOffset, true);
        
        super.renderContent(matrix, control, contentRect, realContentRect, mouseX, mouseY);
    }
    
    @Override
    @OnlyIn(value = Dist.CLIENT)
    protected void renderContent(PoseStack matrix, GuiChildControl control, Rect rect, int mouseX, int mouseY) {}
    
    @Override
    public boolean isContainer() {
        return getParent().isContainer();
    }
    
    @Override
    public void init() {
        for (int i = 0; i < controls.size(); i++)
            controls.get(i).control.init();
    }
    
    @Override
    public void closed() {
        for (int i = 0; i < controls.size(); i++)
            controls.get(i).control.closed();
    }
    
    @Override
    public void tick() {
        for (int i = 0; i < controls.size(); i++)
            controls.get(i).control.tick();
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
    public GuiTooltipEvent getTooltipEvent(Rect rect, double x, double y) {
        GuiTooltipEvent event = super.getTooltipEvent(rect, x, y);
        if (event != null)
            return event;
        
        x *= getScaleFactor();
        y *= getScaleFactor();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        for (GuiChildControl child : controls)
            if (child.control.isInteractable() && child.isMouseOver(x, y)) {
                event = child.control.getTooltipEvent(child.rect, x - child.getX(), y - child.getY());
                if (event != null)
                    return event;
            }
        return null;
    }
    
    @Override
    public boolean testForDoubleClick(Rect rect, double x, double y) {
        x *= getScaleFactor();
        y *= getScaleFactor();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        for (GuiChildControl child : controls)
            if (child.control.isInteractable() && child.control.testForDoubleClick(child.rect, x - child.getX(), y - child.getY()))
                return true;
        return false;
        
    }
    
    @Override
    public void mouseMoved(Rect rect, double x, double y) {
        x *= getScaleFactor();
        y *= getScaleFactor();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        for (GuiChildControl child : controls)
            if (child.control.isInteractable())
                child.control.mouseMoved(child.rect, x - child.getX(), y - child.getY());
    }
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        x *= getScaleFactor();
        y *= getScaleFactor();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        boolean result = false;
        for (GuiChildControl child : controls)
            if (!result && child.control.isInteractable() && child.isMouseOver(x, y) && child.control.mouseClicked(child.rect, x - child.getX(), y - child.getY(), button)) {
                raiseEvent(new GuiControlClickEvent(child.control, button, false));
                result = true;
            } else
                child.control.looseFocus();
        return result;
    }
    
    @Override
    public boolean mouseDoubleClicked(Rect rect, double x, double y, int button) {
        x *= getScaleFactor();
        y *= getScaleFactor();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        boolean result = false;
        for (GuiChildControl child : controls)
            if (!result && child.control.isInteractable() && child.isMouseOver(x, y) && child.control.mouseDoubleClicked(child.rect, x - child.getX(), y - child.getY(), button)) {
                raiseEvent(new GuiControlClickEvent(child.control, button, false));
                result = true;
            } else
                child.control.looseFocus();
        return result;
        
    }
    
    @Override
    public void mouseReleased(Rect rect, double x, double y, int button) {
        x *= getScaleFactor();
        y *= getScaleFactor();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        for (GuiChildControl child : controls)
            if (child.control.isInteractable())
                child.control.mouseReleased(child.rect, x - child.getX(), y - child.getY(), button);
    }
    
    @Override
    public void mouseDragged(Rect rect, double x, double y, int button, double dragX, double dragY, double time) {
        x *= getScaleFactor();
        y *= getScaleFactor();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        for (GuiChildControl child : controls)
            if (child.control.isInteractable())
                child.control.mouseDragged(child.rect, x - child.getX(), y - child.getY(), button, dragX, dragY, time);
    }
    
    @Override
    public boolean mouseScrolled(Rect rect, double x, double y, double delta) {
        x *= getScaleFactor();
        y *= getScaleFactor();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        for (GuiChildControl child : controls)
            if (child.control.isInteractable() && child.isMouseOver(x, y) && child.control.mouseScrolled(child.rect, x - child.getX(), y - child.getY(), delta))
                return true;
        return false;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (GuiChildControl child : controls)
            if (child.control.isInteractable() && child.control.keyPressed(keyCode, scanCode, modifiers))
                return true;
        return false;
    }
    
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (GuiChildControl child : controls)
            if (child.control.isInteractable() && child.control.keyReleased(keyCode, scanCode, modifiers))
                return true;
        return false;
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (GuiChildControl child : controls)
            if (child.control.isInteractable() && child.control.charTyped(codePoint, modifiers))
                return true;
        return false;
    }
    
    @Override
    public void looseFocus() {
        for (GuiChildControl child : controls)
            child.control.looseFocus();
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
    public String getNestedName() {
        if (name.isBlank()) {
            if (getParent() instanceof GuiControl)
                return ((GuiControl) getParent()).getNestedName();
            return "";
        }
        return super.getNestedName();
    }
    
}
