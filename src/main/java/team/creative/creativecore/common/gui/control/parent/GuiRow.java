package team.creative.creativecore.common.gui.control.parent;

import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.flow.GuiFlow;

public class GuiRow extends GuiParent {
    
    public GuiRow(IGuiParent parent) {
        super(parent, GuiFlow.STACK_X);
        setSpacing(0);
        setExpandableX();
        setAlign(Align.STRETCH);
        setVAlign(VAlign.STRETCH);
    }
    
    public GuiRow(IGuiParent parent, GuiColumn... cols) {
        this(parent);
        for (int i = 0; i < cols.length; i++)
            addColumn(cols[i]);
    }
    
    public GuiRow addColumn(GuiColumn col) {
        super.add(col);
        return this;
    }
    
    public GuiColumn removeCol(int index) {
        return (GuiColumn) remove(index);
    }
    
    public GuiColumn getCol(int index) {
        return (GuiColumn) get(index);
    }
    
    public int colCount() {
        return size();
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
    
}
