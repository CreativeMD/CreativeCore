package team.creative.creativecore.common.gui;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.lang3.ArrayUtils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.gui.event.*;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.type.itr.ConsecutiveIterator;

public class GuiParent extends GuiControl implements IGuiParent, Iterable<GuiChildControl> {
    
    private GuiEventManager eventManager;
    protected List<GuiChildControl> controls = new CopyOnWriteArrayList<>();
    protected List<GuiChildControl> hoverControls = new CopyOnWriteArrayList<>();
    
    public GuiFlow flow;
    public Align align = Align.LEFT;
    public VAlign valign = VAlign.TOP;
    public int spacing = 2;
    
    private double scale = 1;
    private double scaleInv = 1;
    
    public GuiParent(String name, GuiFlow flow) {
        super(name);
        this.setFlow(flow);
    }
    
    public GuiParent(String name, GuiFlow flow, VAlign valign) {
        this(name, flow, Align.LEFT, valign);
    }

    public GuiParent(String name, GuiFlow flow, Align align) {
        this(name, flow, align, VAlign.TOP);
    }
    
    public GuiParent(String name, GuiFlow flow, Align align, VAlign valign) {
        this(name, flow);
        this.setAlign(align);
        this.setVAlign(valign);
    }
    
    public GuiParent(String name) {
        this(name, GuiFlow.STACK_X);
    }
    
    public GuiParent() {
        this("");
    }
    
    public GuiParent(GuiFlow flow) {
        this("", flow);
    }
    
    @Override
    public boolean isClient() {
        return this.getParent().isClient();
    }
    
    public GuiParent setScale(double scale) {
        this.scale = scale;
        this.scaleInv = 1 / scale;
        return this;
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
    
    public GuiParent setAlign(Align align) {
        this.align = align;
        return this;
    }
    
    public GuiParent setVAlign(VAlign valign) {
        this.valign = valign;
        return this;
    }

    public GuiParent setSpacing(int spacing) {
        this.spacing = spacing;
        return this;
    }

    public GuiParent setFlow(GuiFlow flow) {
        this.flow = flow;
        return this;
    }

    @Override
    public boolean isExpandableX() {
        if (super.isExpandableX())
            return true;
        for (GuiChildControl child : controls)
            if (child.control.isExpandableX())
                return true;
        return false;
    }
    
    @Override
    public boolean isExpandableY() {
        if (super.isExpandableY())
            return true;
        for (GuiChildControl child : controls)
            if (child.control.isExpandableY())
                return true;
        return false;
    }
    
    private static GuiControl get(String name, List<GuiChildControl> collection) {
        for (int i = 0; i < collection.size(); i++) {
            GuiControl control = collection.get(i).control;
            if (control.name.equalsIgnoreCase(name))
                return control;
            else if (control instanceof GuiParent parent) {
                if (control.name.isBlank()) {
                    GuiControl result = parent.get(name);
                    if (result != null)
                        return result;
                } else if (name.startsWith(control.name + "."))
                    return parent.get(name.substring(control.name.length() + 1));
            }
        }
        return null;
    }
    
    public <T extends GuiControl> T get(String name) {
        if (name.isBlank())
            return (T) this;
        GuiControl result = get(name, controls);
        if (result != null)
            return (T) result;
        return (T) get(name, hoverControls);
    }
    
    public <T extends GuiControl> T get(String name, Class<T> clazz) {
        T result = get(name);
        if (clazz.isInstance(result))
            return result;
        return null;
    }
    
    public boolean has(String name) {
        return get(name) != null;
    }
    
    public GuiChildControl addControl(GuiControl control) {
        control.setParent(this);
        GuiChildControl child = new GuiChildControl(control);
        controls.add(child);
        return child;
    }

    public GuiParent add(GuiControl control) {
        this.addControl(control);
        return this;
    }

    public GuiParent add(GuiControl... controls) {
        for (GuiControl c: controls) {
            this.addControl(c);
        }
        return this;
    }

    public GuiParent add(boolean conditional, Supplier<GuiControl> controlSupplier) {
        if (conditional)
            return this.add(controlSupplier.get());
        return this;
    }
    
    public GuiChildControl addHoverControl(GuiControl control) {
        control.setParent(this);
        GuiChildControl child = new GuiChildControl(control);
        hoverControls.add(child);
        return child;
    }

    public GuiParent addHover(GuiControl control) {
        this.addHoverControl(control);
        return this;
    }

    public GuiParent addHover(GuiControl... controls) {
        for (GuiControl c: controls) {
            this.addHoverControl(c);
        }
        return this;
    }

    public GuiParent addHover(boolean conditional, Supplier<GuiControl> controlSupplier) {
        if (conditional)
            return this.addHover(controlSupplier.get());
        return this;
    }
    
    public boolean remove(GuiChildControl control) {
        return controls.remove(control) || hoverControls.remove(control);
    }
    
    public GuiChildControl find(GuiControl control) {
        for (GuiChildControl child : controls)
            if (child.control == control)
                return child;
            
        for (GuiChildControl child : hoverControls)
            if (child.control == control)
                return child;
        return null;
    }
    
    public GuiChildControl replace(GuiControl oldControl, GuiControl newControl) {
        GuiChildControl child;
        
        for (int i = 0; i < controls.size(); i++) {
            if (controls.get(i).control == oldControl) {
                controls.set(i, child = new GuiChildControl(newControl));
                return child;
            }
        }
        for (int i = 0; i < hoverControls.size(); i++) {
            if (hoverControls.get(i).control == oldControl) {
                hoverControls.set(i, child = new GuiChildControl(newControl));
                return child;
            }
        }
        return null;
    }
    
    public GuiChildControl remove(GuiControl control) {
        for (int i = 0; i < controls.size(); i++) {
            GuiChildControl child = controls.get(i);
            if (child.control == control) {
                controls.remove(i);
                return child;
            }
        }
        for (int i = 0; i < hoverControls.size(); i++) {
            GuiChildControl child = hoverControls.get(i);
            if (child.control == control) {
                hoverControls.remove(i);
                return child;
            }
        }
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
        if (hoverControls.isEmpty()) // Performance optimisation
            return controls.iterator();
        return new ConsecutiveIterator<>(hoverControls, controls);
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderControls(GuiGraphics graphics, Rect contentRect, Rect realContentRect, int mouseX, int mouseY, ListIterator<GuiChildControl> collection, double scale, double xOffset, double yOffset, boolean hover) {
        PoseStack pose = graphics.pose();
        
        while (collection.hasPrevious()) {
            GuiChildControl child = collection.previous();
            GuiControl control = child.control;
            
            if (!control.visible)
                continue;
            
            Rect controlRect = control.createChildRect(child, contentRect, scale, xOffset, yOffset);
            Rect realRect = realContentRect.intersection(controlRect);
            if (realRect != null || hover) {
                if (hover)
                    RenderSystem.disableScissor();
                else
                    realRect.scissor();
                
                pose.pushPose();
                pose.translate(child.getX() + xOffset, child.getY() + yOffset, 10);
                renderControl(graphics, child, control, controlRect, realRect, scale, mouseX, mouseY, hover);
                pose.popPose();
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderControl(GuiGraphics graphics, GuiChildControl child, GuiControl control, Rect controlRect, Rect realRect, double scale, int mouseX, int mouseY, boolean hover) {
        control.render(graphics, child, controlRect, hover ? controlRect : realRect, scale, mouseX, mouseY);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderContent(GuiGraphics graphics, GuiChildControl control, Rect contentRect, Rect realContentRect, double scale, int mouseX, int mouseY) {
        if (realContentRect == null)
            return;
        
        PoseStack pose = graphics.pose();
        float controlScale = (float) scaleFactor();
        scale *= scaleFactor();
        double xOffset = getOffsetX();
        double yOffset = getOffsetY();
        
        pose.scale(controlScale, controlScale, 1);
        
        renderControls(graphics, contentRect, realContentRect, mouseX, mouseY, controls.listIterator(controls.size()), scale, xOffset, yOffset, false);
        renderControls(graphics, contentRect, realContentRect, mouseX, mouseY, hoverControls.listIterator(hoverControls.size()), scale, xOffset, yOffset, true);
        
        super.renderContent(graphics, control, contentRect, realContentRect, scale, mouseX, mouseY);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderContent(GuiGraphics graphics, GuiChildControl control, Rect rect, int mouseX, int mouseY) {}
    
    @Override
    public boolean isContainer() {
        return getParent().isContainer();
    }
    
    @Override
    public void init() {
        for (GuiChildControl child : this)
            child.control.init();
    }
    
    @Override
    public void closed() {
        for (GuiChildControl child : this)
            child.control.closed();
    }
    
    @Override
    public void tick() {
        for (GuiChildControl child : this)
            child.control.tick();
    }
    
    @Override
    public void closeTopLayer() {
        getParent().closeTopLayer();
    }
    
    public void closeThisLayer() {
        getParent().closeLayer(getLayer());
    }
    
    @Override
    public void closeLayer(GuiLayer layer) {
        getParent().closeLayer(layer);
    }
    
    public boolean isMouseOverHovered(double x, double y) {
        x *= scaleFactorInv();
        y *= scaleFactorInv();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        for (GuiChildControl child : hoverControls)
            if (child.isMouseOver(x, y))
                return true;
        return false;
    }
    
    @Override
    public Rect toLayerRect(GuiControl control, Rect rect) {
        GuiChildControl child = find(control);
        if (child == null)
            return rect;
        rect.move(child.rect.minX + getOffsetX() + getContentOffset(), child.rect.minY + getOffsetY() + getContentOffset());
        rect.scale(scaleFactor());
        return getParent().toLayerRect(this, rect);
    }
    
    @Override
    public Rect toScreenRect(GuiControl control, Rect rect) {
        GuiChildControl child = find(control);
        if (child == null)
            return rect;
        rect.move(child.rect.minX + getOffsetX() + getContentOffset(), child.rect.minY + getOffsetY() + getContentOffset());
        rect.scale(scaleFactor());
        return getParent().toScreenRect(this, rect);
    }
    
    @Override
    public GuiTooltipEvent getTooltipEvent(Rect rect, double x, double y) {
        GuiTooltipEvent event = super.getTooltipEvent(rect, x, y);
        if (event != null)
            return event;
        
        x *= scaleFactorInv();
        y *= scaleFactorInv();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        for (GuiChildControl child : this)
            if (child.control.isInteractable() && child.isMouseOver(x, y)) {
                event = child.control.getTooltipEvent(child.rect, x - child.getX(), y - child.getY());
                if (event != null)
                    return event;
            }
        return null;
    }
    
    @Override
    public boolean testForDoubleClick(Rect rect, double x, double y, int button) {
        x *= scaleFactorInv();
        y *= scaleFactorInv();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        for (GuiChildControl child : this)
            if (child.control.isInteractable() && child.rect.inside(x, y) && child.control.testForDoubleClick(child.rect, x - child.getX(), y - child.getY(), button))
                return true;
        return false;
        
    }
    
    @Override
    public void mouseMoved(Rect rect, double x, double y) {
        x *= scaleFactorInv();
        y *= scaleFactorInv();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        for (GuiChildControl child : this)
            if (child.control.isInteractable())
                child.control.mouseMoved(child.rect, x - child.getX(), y - child.getY());
    }
    
    @Override
    public boolean mouseClicked(Rect rect, double x, double y, int button) {
        x *= scaleFactorInv();
        y *= scaleFactorInv();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        boolean result = false;
        for (GuiChildControl child : this)
            if (!result && child.control.isInteractable() && child.isMouseOver(x, y) && child.control.mouseClicked(child.rect, x - child.getX(), y - child.getY(), button)) {
                raiseEvent(new GuiControlClickEvent(child.control, button, false));
                result = true;
            } else
                child.control.looseFocus();
        return result;
    }
    
    @Override
    public boolean mouseDoubleClicked(Rect rect, double x, double y, int button) {
        x *= scaleFactorInv();
        y *= scaleFactorInv();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        boolean result = false;
        for (GuiChildControl child : this)
            if (!result && child.control.isInteractable() && child.isMouseOver(x, y) && child.control.mouseDoubleClicked(child.rect, x - child.getX(), y - child.getY(), button)) {
                raiseEvent(new GuiControlClickEvent(child.control, button, false));
                result = true;
            } else
                child.control.looseFocus();
        return result;
        
    }
    
    @Override
    public void mouseReleased(Rect rect, double x, double y, int button) {
        x *= scaleFactorInv();
        y *= scaleFactorInv();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        for (GuiChildControl child : this)
            if (child.control.isInteractable())
                child.control.mouseReleased(child.rect, x - child.getX(), y - child.getY(), button);
    }
    
    @Override
    public void mouseDragged(Rect rect, double x, double y, int button, double dragX, double dragY, double time) {
        x *= scaleFactorInv();
        y *= scaleFactorInv();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        for (GuiChildControl child : this)
            if (child.control.isInteractable())
                child.control.mouseDragged(child.rect, x - child.getX(), y - child.getY(), button, dragX, dragY, time);
    }
    
    @Override
    public boolean mouseScrolled(Rect rect, double x, double y, double delta) {
        x *= scaleFactorInv();
        y *= scaleFactorInv();
        int offset = getContentOffset();
        x += -getOffsetX() - offset;
        y += -getOffsetY() - offset;
        for (GuiChildControl child : this)
            if (child.control.isInteractable() && child.isMouseOver(x, y) && child.control.mouseScrolled(child.rect, x - child.getX(), y - child.getY(), delta))
                return true;
        return false;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (GuiChildControl child : this)
            if (child.control.isInteractable() && child.control.keyPressed(keyCode, scanCode, modifiers))
                return true;
        return false;
    }
    
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (GuiChildControl child : this)
            if (child.control.isInteractable() && child.control.keyReleased(keyCode, scanCode, modifiers))
                return true;
        return false;
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (GuiChildControl child : this)
            if (child.control.isInteractable() && child.control.charTyped(codePoint, modifiers))
                return true;
        return false;
    }
    
    @Override
    public void looseFocus() {
        for (GuiChildControl child : this)
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
    
    public void clearEvents() {
        if (eventManager != null)
            eventManager.clear();
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
    
    @Override
    public void flowX(int width, int preferred) {
        flow.flowX(controls, spacing, align, width, preferred, endlessX());
    }
    
    @Override
    public void flowY(int width, int height, int preferred) {
        flow.flowY(controls, spacing, valign, width, height, preferred, endlessY());
    }
    
    protected boolean endlessX() {
        return false;
    }
    
    protected boolean endlessY() {
        return false;
    }
    
    @Override
    protected int minWidth(int availableWidth) {
        return flow.minWidth(controls, spacing, availableWidth);
    }
    
    @Override
    protected int preferredWidth(int availableWidth) {
        return flow.preferredWidth(controls, spacing, availableWidth);
    }
    
    @Override
    protected int minHeight(int width, int availableHeight) {
        return flow.minHeight(controls, spacing, width, availableHeight);
    }
    
    @Override
    protected int preferredHeight(int width, int availableHeight) {
        return flow.preferredHeight(controls, spacing, width, availableHeight);
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT;
    }
    
}
