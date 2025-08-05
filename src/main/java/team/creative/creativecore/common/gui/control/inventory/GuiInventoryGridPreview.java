package team.creative.creativecore.common.gui.control.inventory;

import net.minecraft.world.Container;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.IGuiParent;

public class GuiInventoryGridPreview extends GuiParent {
    
    public final Container container;
    protected boolean hasFixedSize = false;
    private int fixedSize;
    
    public GuiInventoryGridPreview(IGuiParent parent, String name, Container container) {
        this(parent, name, container, (int) Math.ceil(Math.sqrt(container.getContainerSize())));
        this.hasFixedSize = false;
    }
    
    public GuiInventoryGridPreview(IGuiParent parent, String name, Container container, int cols) {
        this(parent, name, container, cols, (int) Math.ceil(container.getContainerSize() / (double) cols));
    }
    
    public GuiInventoryGridPreview(IGuiParent parent, String name, Container container, int cols, int rows) {
        super(parent, name);
        this.hasFixedSize = true;
        if (dist() != null)
            dist().setGridDim(cols, rows);
        this.container = container;
        this.fixedSize = Math.min(container.getContainerSize(), cols * rows);
        for (int i = 0; i < fixedSize; i++)
            super.add(new GuiSlotViewer(this, container.getItem(i)));
    }
    
    @Override
    public GuiInventoryGridPreviewDist dist() {
        return (GuiInventoryGridPreviewDist) super.dist();
    }
    
    public boolean hasFixedSize() {
        return hasFixedSize;
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
    
    public static interface GuiInventoryGridPreviewDist extends GuiParentDistHandler {
        
        public void setGridDim(int cols, int rows);
        
    }
}
