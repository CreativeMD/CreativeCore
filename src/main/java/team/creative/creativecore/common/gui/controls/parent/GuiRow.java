package team.creative.creativecore.common.gui.controls.parent;

import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.flow.GuiFlow;

public class GuiRow extends GuiParent {
    
    public GuiRow() {
        super(GuiFlow.STACK_X);
        this.spacing = 0;
        setExpandableX();
        align = Align.STRETCH;
        valign = VAlign.STRETCH;
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
    
    public GuiColumn removeCol(int index) {
        return (GuiColumn) controls.remove(index).control;
    }
    
    public GuiColumn getCol(int index) {
        return (GuiColumn) controls.get(index).control;
    }
    
    public int colCount() {
        return controls.size();
    }
    
    @Override
    @Deprecated
    public GuiChildControl add(GuiControl control) {
        throw new UnsupportedOperationException();
    }
    
}
