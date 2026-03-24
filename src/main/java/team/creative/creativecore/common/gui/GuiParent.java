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
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.event.GuiControlClickEvent;
import team.creative.creativecore.common.gui.event.GuiEvent;
import team.creative.creativecore.common.gui.event.GuiEventManager;
import team.creative.creativecore.common.gui.event.GuiTooltipEvent;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.math.geo.Rect;
import team.creative.creativecore.common.util.type.itr.ConsecutiveIterator;

public class GuiParent extends GuiControl implements IGuiParent, Iterable<GuiControl> {
    
    private static GuiControl get(String name, List<GuiControl> collection) {
        for (int i = 0; i < collection.size(); i++) {
            GuiControl control = collection.get(i);
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
    
    private GuiEventManager eventManager;
    protected List<GuiControl> controls = new CopyOnWriteArrayList<>();
    protected List<GuiControl> hoverControls = new CopyOnWriteArrayList<>();
    
    public GuiFlow flow;
    public Align align = Align.LEFT;
    public VAlign valign = VAlign.TOP;
    public int spacing = 2;
    
    private double scale = 1;
    private double scaleInv = 1;
    
    public GuiParent(String name, GuiFlow flow) {
        super(name);
        this.flow = flow;
    }
    
    public GuiParent(String name, GuiFlow flow, VAlign valign) {
        this(name, flow, Align.LEFT, valign);
    }
    
    public GuiParent(String name, GuiFlow flow, Align align) {
        this(name, flow, align, VAlign.TOP);
    }
    
    public GuiParent(String name, GuiFlow flow, Align align, VAlign valign) {
        this(name, flow);
        this.align = align;
        this.valign = valign;
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
        for (GuiControl control : controls)
            if (control.isExpandableX())
                return true;
        return false;
    }
    
    @Override
    public boolean isExpandableY() {
        if (super.isExpandableY())
            return true;
        for (GuiControl control : controls)
            if (control.isExpandableY())
                return true;
        return false;
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
    
    /** inserts the given the control before the parameter
     *
     * @param reference
     *            the reference to search for the correct position
     * @param toInsert
     *            the control to be added
     * @return null if the reference could not be found */
    public <T extends GuiControl> T insertControlBefore(GuiControl reference, T toInsert) {
        int index = controls.indexOf(reference);
        if (index == -1)
            return null;
        toInsert.setParent(this);
        controls.add(index, toInsert);
        return toInsert;
    }
    
    /** inserts the given the control after the parameter
     *
     * @param reference
     *            the reference to search for the correct position
     * @param toInsert
     *            the control to be added
     * @return null if the reference could not be found */
    public <T extends GuiControl> T insertControlAfter(GuiControl reference, T toInsert) {
        int index = controls.indexOf(reference);
        if (index == -1)
            return null;
        toInsert.setParent(this);
        index++;
        if (index == controls.size())
            controls.add(toInsert);
        else
            controls.add(index, toInsert);
        return toInsert;
    }
    
    public boolean contains(GuiControl control) {
        return controls.contains(control) || hoverControls.contains(control);
    }
    
    public GuiParent add(GuiControl control) {
        control.setParent(this);
        controls.add(control);
        return this;
    }
    
    public GuiParent add(GuiControl... controls) {
        for (GuiControl c : controls)
            add(c);
        return this;
    }
    
    public GuiParent add(boolean conditional, Supplier<GuiControl> controlSupplier) {
        if (conditional)
            return this.add(controlSupplier.get());
        return this;
    }
    
    public GuiParent addHover(GuiControl control) {
        control.setParent(this);
        hoverControls.add(control);
        return this;
    }
    
    public GuiParent addHover(GuiControl... controls) {
        for (GuiControl c : controls)
            addHover(c);
        return this;
    }
    
    public GuiParent addHover(boolean conditional, Supplier<GuiControl> controlSupplier) {
        if (conditional)
            return this.addHover(controlSupplier.get());
        return this;
    }
    
    public boolean remove(GuiControl control) {
        return controls.remove(control) || hoverControls.remove(control);
    }
    
    public boolean replace(GuiControl oldControl, GuiControl newControl) {
        for (int i = 0; i < controls.size(); i++)
            if (controls.get(i) == oldControl) {
                newControl.setParent(this);
                controls.set(i, newControl);
                return true;
            }
        for (int i = 0; i < hoverControls.size(); i++)
            if (hoverControls.get(i) == oldControl) {
                hoverControls.set(i, newControl);
                return true;
            }
        return false;
    }
    
    public void remove(String... include) {
        controls.removeIf((x) -> ArrayUtils.contains(include, x.name));
        hoverControls.removeIf((x) -> ArrayUtils.contains(include, x.name));
    }
    
    public void removeExclude(String... exclude) {
        controls.removeIf((x) -> !ArrayUtils.contains(exclude, x.name));
        hoverControls.removeIf((x) -> !ArrayUtils.contains(exclude, x.name));
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
    public Iterator<GuiControl> iterator() {
        if (hoverControls.isEmpty()) // Performance optimisation
            return controls.iterator();
        return new ConsecutiveIterator<>(hoverControls, controls);
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderControls(GuiGraphics graphics, Rect contentRect, Rect realContentRect, int mouseX, int mouseY, ListIterator<GuiControl> collection, double scale,
            double xOffset, double yOffset, boolean hover) {
        PoseStack pose = graphics.pose();
        
        while (collection.hasPrevious()) {
            GuiControl control = collection.previous();
            
            if (!control.visible)
                continue;
            
            Rect controlContentRect = control.createChildRect(contentRect, scale, xOffset, yOffset);
            Rect realRect = realContentRect.intersection(controlContentRect);
            if (realRect != null || hover) {
                if (hover)
                    RenderSystem.disableScissor();
                else
                    realRect.scissor();
                
                pose.pushPose();
                pose.translate(control.rect.getX() + xOffset, control.rect.getY() + yOffset, 10);
                renderControl(graphics, control, controlContentRect, realRect, scale, mouseX, mouseY, hover);
                pose.popPose();
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderControl(GuiGraphics graphics, GuiControl control, Rect controlContentRect, Rect realRect, double scale, int mouseX, int mouseY, boolean hover) {
        control.render(graphics, controlContentRect, hover ? controlContentRect : realRect, scale, mouseX, mouseY);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderContent(GuiGraphics graphics, Rect contentRect, Rect realContentRect, double scale, int mouseX, int mouseY) {
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
        
        super.renderContent(graphics, contentRect, realContentRect, scale, mouseX, mouseY);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    @OnlyIn(Dist.CLIENT)
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY) {}
    
    @Override
    public boolean isContainer() {
        return getParent().isContainer();
    }
    
    @Override
    public void init() {
        for (GuiControl control : this)
            control.init();
    }
    
    @Override
    public void closed() {
        for (GuiControl control : this)
            control.closed();
    }
    
    @Override
    public void tick() {
        for (GuiControl control : this)
            control.tick();
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
        for (GuiControl control : hoverControls)
            if (control.rect.inside(x, y))
                return true;
        return false;
    }
    
    @Override
    public Rect toLayerRect(GuiControl control, Rect rect) {
        rect.move(control.rect.getX() + getOffsetX() + getContentOffset(), control.rect.getY() + getOffsetY() + getContentOffset());
        rect.scale(scaleFactor());
        return getParent().toLayerRect(this, rect);
    }
    
    @Override
    public Rect toScreenRect(GuiControl control, Rect rect) {
        rect.move(control.rect.getX() + getOffsetX() + getContentOffset(), control.rect.getY() + getOffsetY() + getContentOffset());
        rect.scale(scaleFactor());
        return getParent().toScreenRect(this, rect);
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
        for (GuiControl control : this)
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
        for (GuiControl control : this)
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
        for (GuiControl control : this)
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
        for (GuiControl control : this)
            if (!result && control.isInteractable() && control.rect.inside(x, y) && control.mouseClicked(x - control.rect.getX(), y - control.rect.getY(), button)) {
                raiseEvent(new GuiControlClickEvent(control, button, false));
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
        for (GuiControl control : this)
            if (!result && control.isInteractable() && control.rect.inside(x, y) && control.mouseDoubleClicked(x - control.rect.getX(), y - control.rect.getY(), button)) {
                raiseEvent(new GuiControlClickEvent(control, button, false));
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
        for (GuiControl control : this)
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
        for (GuiControl control : this)
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
        for (GuiControl control : this)
            if (control.isInteractable() && control.rect.inside(x, y) && control.mouseScrolled(x - control.rect.getX(), y - control.rect.getY(), delta))
                return true;
        return false;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (GuiControl control : this)
            if (control.isInteractable() && control.keyPressed(keyCode, scanCode, modifiers))
                return true;
        return false;
    }
    
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (GuiControl control : this)
            if (control.isInteractable() && control.keyReleased(keyCode, scanCode, modifiers))
                return true;
        return false;
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (GuiControl control : this)
            if (control.isInteractable() && control.charTyped(codePoint, modifiers))
                return true;
        return false;
    }
    
    @Override
    public void looseFocus() {
        for (GuiControl control : this)
            control.looseFocus();
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
        flow.flowX(controls.stream().map(x -> x.rect).toList(), spacing, align, (int) (width / scale), preferred, endlessX());
    }
    
    @Override
    public void flowY(int width, int height, int preferred) {
        flow.flowY(controls.stream().map(x -> x.rect).toList(), spacing, valign, (int) (width / scale), (int) (height / scale), preferred, endlessY());
    }
    
    protected boolean endlessX() {
        return false;
    }
    
    protected boolean endlessY() {
        return false;
    }
    
    @Override
    protected int minWidth(int availableWidth) {
        return Mth.ceil(flow.minWidth(controls.stream().map(x -> x.rect).toList(), spacing, availableWidth) * scale);
    }
    
    @Override
    protected int preferredWidth(int availableWidth) {
        return Mth.ceil(flow.preferredWidth(controls.stream().map(x -> x.rect).toList(), spacing, availableWidth) * scale);
    }
    
    @Override
    protected int minHeight(int width, int availableHeight) {
        return Mth.ceil(flow.minHeight(controls.stream().map(x -> x.rect).toList(), spacing, width, availableHeight) * scale);
    }
    
    @Override
    protected int preferredHeight(int width, int availableHeight) {
        return Mth.ceil(flow.preferredHeight(controls.stream().map(x -> x.rect).toList(), spacing, width, availableHeight) * scale);
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT;
    }
    
}
