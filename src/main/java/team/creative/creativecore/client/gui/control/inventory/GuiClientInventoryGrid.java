package team.creative.creativecore.client.gui.control.inventory;

import team.creative.creativecore.client.gui.GuiClientControl;
import team.creative.creativecore.client.gui.GuiClientParent;
import team.creative.creativecore.common.gui.control.inventory.GuiInventoryGrid;
import team.creative.creativecore.common.gui.control.inventory.GuiInventoryGrid.GuiInventoryGridDist;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public class GuiClientInventoryGrid<T extends GuiInventoryGrid> extends GuiClientParent<T> implements GuiInventoryGridDist {
    
    protected boolean reverse = false;
    private int cols;
    private int rows;
    
    private int cachedCols;
    private int cachedRows;
    
    public GuiClientInventoryGrid(T control) {
        super(control);
    }
    
    @Override
    public void setGridDim(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
    }
    
    @Override
    protected int minWidth(int availableWidth) {
        return control.hasFixedSize() ? cols * GuiClientSlotBase.SLOT_SIZE : GuiClientSlotBase.SLOT_SIZE;
    }
    
    @Override
    protected int minHeight(int width, int availableHeight) {
        return control.hasFixedSize() ? rows * GuiClientSlotBase.SLOT_SIZE : GuiClientSlotBase.SLOT_SIZE;
    }
    
    @Override
    public void flowX(int width, int preferred) {
        cachedCols = width / GuiClientSlotBase.SLOT_SIZE;
        if (control.hasFixedSize())
            cachedCols = Math.min(cachedCols, this.cols);
        int offset = (width - cachedCols * GuiClientSlotBase.SLOT_SIZE) / 2;
        int i = 0;
        for (GuiClientControl control : controls()) {
            control.rect.setX(offset + (i % cachedCols) * GuiClientSlotBase.SLOT_SIZE);
            control.rect.setWidth(GuiClientSlotBase.SLOT_SIZE, width);
            control.rect.flowX();
            i++;
        }
    }
    
    @Override
    public void flowY(int witdh, int height, int preferred) {
        cachedRows = height / GuiClientSlotBase.SLOT_SIZE;
        if (control.hasFixedSize())
            cachedRows = Math.min(cachedRows, this.rows);
        int offset = (height - cachedRows * GuiClientSlotBase.SLOT_SIZE) / 2;
        int i = reverse ? control.size() - 1 : 0;
        for (GuiClientControl control : controls()) {
            int row = i / cachedCols;
            control.rect.setY(offset + row * GuiClientSlotBase.SLOT_SIZE);
            control.rect.setHeight(GuiClientSlotBase.SLOT_SIZE, height);
            control.rect.flowY();
            control.visible = row <= cachedRows;
            if (reverse)
                i--;
            else
                i++;
        }
    }
    
    @Override
    protected int preferredWidth(int availableWidth) {
        return cols * GuiClientSlotBase.SLOT_SIZE;
    }
    
    @Override
    protected int preferredHeight(int width, int availableHeight) {
        return (int) Math.ceil(control.container.getContainerSize() / (double) cachedCols) * GuiClientSlotBase.SLOT_SIZE;
    }
    
    @Override
    protected ControlFormatting defaultFormatting() {
        return ControlFormatting.TRANSPARENT;
    }
}
