package team.creative.creativecore.common.gui.control.inventory;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.IGuiParent;

public class GuiInventoryGrid extends GuiParent implements IGuiInventory {
    
    public final Container container;
    protected boolean hasFixedSize;
    protected int fixedSize;
    private boolean allChanged = false;
    private final BitSet changed = new BitSet();
    private List<Consumer<GuiSlot>> listeners;
    private final List<GuiSlot> slots = new ArrayList<>();
    
    public GuiInventoryGrid(IGuiParent parent, String name, Container container) {
        this(parent, name, container, (int) Math.ceil(Math.sqrt(container.getContainerSize())));
        this.hasFixedSize = false;
    }
    
    public GuiInventoryGrid(IGuiParent parent, String name, Container container, int cols) {
        this(parent, name, container, cols, (int) Math.ceil(container.getContainerSize() / (double) cols));
    }
    
    public GuiInventoryGrid(IGuiParent parent, String name, Container container, int cols, int rows) {
        this(parent, name, container, cols, rows, (c, i) -> new Slot(c, i, 0, 0));
    }
    
    public GuiInventoryGrid(IGuiParent parent, String name, Container container, int cols, int rows, BiFunction<Container, Integer, Slot> slotFactory) {
        super(parent, name);
        this.hasFixedSize = true;
        dist().setGridDim(cols, rows);
        this.container = container;
        this.fixedSize = Math.min(container.getContainerSize(), cols * rows);
        createInventoryGrid(slotFactory);
    }
    
    @Override
    public GuiInventoryGridDist dist() {
        return (GuiInventoryGridDist) super.dist();
    }
    
    protected void createInventoryGrid(BiFunction<Container, Integer, Slot> slotFactory) {
        for (int i = 0; i < fixedSize; i++)
            addSlot(new GuiSlot(getParent(), slotFactory.apply(container, i)));
    }
    
    public GuiInventoryGrid disableSlot(int index) {
        getSlot(index).setEnabled(false);
        return this;
    }
    
    public GuiInventoryGrid addListener(Consumer<GuiSlot> slot) {
        if (listeners == null)
            listeners = new ArrayList<>();
        listeners.add(slot);
        return this;
    }
    
    protected GuiControl addSlot(GuiSlot slot) {
        while (slot.slot.getContainerSlot() >= slots.size())
            slots.add(null);
        slots.set(slot.slot.getContainerSlot(), slot);
        return super.add(slot);
    }
    
    @Override
    @Deprecated
    public GuiParent add(GuiControl control) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    @Deprecated
    public GuiParent addHover(GuiControl control) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public GuiSlot getSlot(int index) {
        return slots.get(index);
    }
    
    @Override
    public int inventorySize() {
        if (hasFixedSize)
            return fixedSize;
        return container.getContainerSize();
    }
    
    public boolean hasFixedSize() {
        return hasFixedSize;
    }
    
    @Override
    public String name() {
        return name;
    }
    
    @Override
    public void tick() {
        if (allChanged) {
            container.setChanged();
            syncAll();
            allChanged = false;
            changed.clear();
        } else if (!changed.isEmpty()) {
            container.setChanged();
            sync(changed);
            changed.clear();
        }
        super.tick();
    }
    
    @Override
    public void setChanged() {
        allChanged = true;
        if (listeners != null)
            for (Consumer<GuiSlot> listener : listeners)
                listener.accept(null);
    }
    
    @Override
    public void setChanged(int slotIndex) {
        if (allChanged)
            return;
        changed.set(slotIndex);
        GuiSlot slot = getSlot(slotIndex);
        if (listeners != null)
            for (Consumer<GuiSlot> listener : listeners)
                listener.accept(slot);
    }
    
    public static interface GuiInventoryGridDist extends GuiParentDistHandler {
        
        public void setGridDim(int cols, int rows);
        
    }
    
}
