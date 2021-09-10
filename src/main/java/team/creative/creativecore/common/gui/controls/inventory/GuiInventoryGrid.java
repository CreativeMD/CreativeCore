package team.creative.creativecore.common.gui.controls.inventory;

import net.minecraft.world.Container;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public class GuiInventoryGrid extends GuiParent {
    
    public final Container container;
    protected boolean hasFixedSize = false;
    private int cols;
    private int rows;
    
    private int cachedCols;
    private int cachedRows;
    
    public GuiInventoryGrid(String name, Container container) {
        this(name, container, (int) Math.ceil(Math.sqrt(container.getContainerSize())));
        this.hasFixedSize = false;
    }
    
    public GuiInventoryGrid(String name, Container container, int cols) {
        this(name, container, cols, (int) Math.ceil(container.getContainerSize() / (double) cols));
    }
    
    public GuiInventoryGrid(String name, Container container, int cols, int rows) {
        super(name);
        this.hasFixedSize = true;
        this.cols = cols;
        this.rows = rows;
        this.container = container;
        int size = Math.min(container.getContainerSize(), cols * rows);
        for (int i = 0; i < size; i++)
            add(new GuiSlot(container, i));
    }
    
    @Override
    @Deprecated
    public GuiChildControl add(GuiControl control) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void flowX(int width, int preferred) {
        cachedCols = width / GuiSlotBase.SLOT_SIZE;
        if (hasFixedSize)
            cachedCols = Math.min(cachedCols, this.cols);
        int offset = (width - cachedCols * GuiSlotBase.SLOT_SIZE) / 2;
        int i = 0;
        for (GuiChildControl control : controls) {
            control.setX(offset + (i % cachedCols) * GuiSlotBase.SLOT_SIZE);
            control.flowX();
            i++;
        }
    }
    
    @Override
    public void flowY(int height, int preferred) {
        cachedRows = height / GuiSlotBase.SLOT_SIZE;
        if (hasFixedSize)
            cachedRows = Math.min(cachedRows, this.rows);
        int offset = (height - cachedRows * GuiSlotBase.SLOT_SIZE) / 2;
        int i = 0;
        for (GuiChildControl control : controls) {
            int row = i / cachedCols;
            control.setY(offset + row * GuiSlotBase.SLOT_SIZE);
            control.flowY();
            if (row > cachedRows)
                control.control.visible = false;
            else
                control.control.visible = true;
            i++;
        }
    }
    
    @Override
    protected int preferredWidth() {
        return cols * GuiSlotBase.SLOT_SIZE;
    }
    
    @Override
    protected int preferredHeight() {
        return (int) Math.ceil(container.getContainerSize() / (double) cachedCols) * GuiSlotBase.SLOT_SIZE;
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT;
    }
    
}
