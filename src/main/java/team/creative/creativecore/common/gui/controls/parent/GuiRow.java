package team.creative.creativecore.common.gui.controls.parent;

import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;

public class GuiRow extends GuiBoxX {
    
    public GuiRow() {
        this.spacing = 0;
    }
    
    public GuiRow(GuiColumn... cols) {
        this();
        for (int i = 0; i < cols.length; i++)
            addColumn(cols[i]);
    }
    
    public GuiRow addColumn(GuiColumn col) {
        super.add(col);
        return this;
    }
    
    @Override
    public GuiChildControl add(GuiControl control) {
        throw new UnsupportedOperationException();
    }
    
}
