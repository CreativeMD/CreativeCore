package team.creative.creativecore.common.gui;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;

import team.creative.creativecore.common.gui.GuiParent.GuiParentDistHandler;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.gui.event.GuiControlClickEvent;
import team.creative.creativecore.common.gui.event.GuiEvent;
import team.creative.creativecore.common.gui.event.GuiEventManager;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.manager.GuiManager;
import team.creative.creativecore.common.gui.manager.GuiManagerDist;
import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiParent<D extends GuiParentDistHandler> extends GuiControl<D> implements IGuiParent, Iterable<GuiControl> {
    
    private GuiEventManager eventManager;
    private final GuiControls controls;
    
    public GuiParent(IGuiParent parent, String name, GuiFlow flow) {
        super(parent, name);
        dist.setFlow(flow);
        dist.initControlList(controls = new GuiControls());
    }
    
    public GuiParent(IGuiParent parent, String name, GuiFlow flow, VAlign valign) {
        this(parent, name, flow, Align.LEFT, valign);
    }
    
    public GuiParent(IGuiParent parent, String name, GuiFlow flow, Align align) {
        this(parent, name, flow, align, VAlign.TOP);
    }
    
    public GuiParent(IGuiParent parent, String name, GuiFlow flow, Align align, VAlign valign) {
        this(parent, name, flow);
        dist.setAlign(align);
        dist.setVAlign(valign);
    }
    
    public GuiParent(IGuiParent parent, String name) {
        this(parent, name, GuiFlow.STACK_X);
    }
    
    public GuiParent(IGuiParent parent) {
        this(parent, "");
    }
    
    public GuiParent(IGuiParent parent, GuiFlow flow) {
        this(parent, "", flow);
    }
    
    @Override
    public boolean isClient() {
        return this.getParent().isClient();
    }
    
    public GuiParent setScale(double scale) {
        dist.setScale(scale);
        return this;
    }
    
    public double getOffsetY() {
        return 0;
    }
    
    public double getOffsetX() {
        return 0;
    }
    
    public GuiParent setAlign(Align align) {
        dist.setAlign(align);
        return this;
    }
    
    public GuiParent setVAlign(VAlign valign) {
        dist.setVAlign(valign);
        return this;
    }
    
    public GuiParent setSpacing(int spacing) {
        dist.setSpacing(spacing);
        return this;
    }
    
    public GuiParent setFlow(GuiFlow flow) {
        dist.setFlow(flow);
        return this;
    }
    
    public <T extends GuiControl> T get(String name) {
        if (name.isBlank())
            return (T) this;
        return controls.get(name);
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
        return controls.insertControlBefore(reference, toInsert);
    }
    
    /** inserts the given the control after the parameter
     *
     * @param reference
     *            the reference to search for the correct position
     * @param toInsert
     *            the control to be added
     * @return null if the reference could not be found */
    public <T extends GuiControl> T insertControlAfter(GuiControl reference, T toInsert) {
        return controls.insertControlAfter(reference, toInsert);
    }
    
    public GuiParent add(GuiControl control) {
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
        controls.addHover(control);
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
        return controls.remove(control);
    }
    
    public boolean replace(GuiControl oldControl, GuiControl newControl) {
        return controls.replace(oldControl, newControl);
    }
    
    public void remove(String... include) {
        controls.remove(include);
    }
    
    public void removeExclude(String... exclude) {
        controls.removeExclude(exclude);
    }
    
    public boolean isEmpty() {
        return controls.isEmpty();
    }
    
    public void clear() {
        controls.clear();
    }
    
    public int size() {
        return controls.totalSize();
    }
    
    @Override
    public Iterator<GuiControl> iterator() {
        return controls.iterator();
    }
    
    @Override
    public boolean isContainer() {
        return getParent().isContainer();
    }
    
    @Override
    public void init() {
        super.init();
        for (GuiControl control : this)
            control.init();
    }
    
    @Override
    public void closed() {
        super.closed();
        for (GuiControl control : this)
            control.closed();
    }
    
    @Override
    public void tick() {
        super.tick();
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
    
    public <E extends GuiEvent> void registerEvent(Class<E> clazz, Consumer<E> action) {
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
    public Rect toLayerRect(GuiControl control, Rect rect) {
        dist.applyOffset(control, rect);
        return getParent().toLayerRect(this, rect);
    }
    
    @Override
    public Rect toScreenRect(GuiControl control, Rect rect) {
        dist.applyOffset(control, rect);
        return getParent().toScreenRect(this, rect);
    }
    
    @Override
    public <T extends GuiControlDistHandler> T createDist(GuiControl<T> control) {
        return getParent().createDist(control);
    }
    
    @Override
    public <T extends GuiManagerDist> T createDist(GuiManager<T> manager) {
        return getParent().createDist(manager);
    }
    
    public static interface GuiParentDistHandler extends GuiControlDistHandler {
        
        public void setScale(double scale);
        
        public void setAlign(Align align);
        
        public void setVAlign(VAlign valign);
        
        public void setSpacing(int spacing);
        
        public void setFlow(GuiFlow flow);
        
        public void initControlList(GuiControls controls);
        
        public void applyOffset(GuiControl control, Rect rect);
    }
    
}
