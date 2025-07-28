package team.creative.creativecore.common.gui.control.parent;

import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiControlDistHandler;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.control.parent.GuiScrollXY.GuiScrollXYDist;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.util.type.itr.FunctionIterator;

public class GuiTableScrollable extends GuiParent {
    
    public final GuiRow topRow;
    public final GuiParent bottom;
    public final GuiParent firstCol;
    public final GuiScrollXY scrollableTable;
    
    public GuiTableScrollable(IGuiParent parent) {
        this(parent, "");
    }
    
    public GuiTableScrollable(IGuiParent parent, String name) {
        super(parent, name);
        topRow = new GuiRow(parent);
        bottom = new GuiParent(parent);
        firstCol = new GuiParent(parent, GuiFlow.STACK_Y);
        scrollableTable = new GuiScrollXY(parent, "", GuiFlow.STACK_Y);
        
        setFlow(GuiFlow.STACK_Y);
        super.add(topRow);
        super.add(bottom);
        bottom.add(firstCol);
        bottom.add(scrollableTable);
        setSpacing(4);
    }
    
    @Override
    public GuiTableScrollableDist dist() {
        return (GuiTableScrollableDist) super.dist();
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
    public GuiTableScrollable setExpandable() {
        return (GuiTableScrollable) super.setExpandable();
    }
    
    public GuiRow getTopRow() {
        return topRow;
    }
    
    public GuiTableScrollable addRow(GuiRow row) {
        if (row.colCount() == 0)
            return this;
        firstCol.add(row.removeCol(0));
        scrollableTable.add(row);
        return this;
    }
    
    @Override
    public boolean isExpandableX() {
        return dist().isExpandableX();
    }
    
    @Override
    public boolean isExpandableY() {
        return dist().isExpandableY();
    }
    
    public void removeContentCol(int index) {
        topRow.removeCol(index + 1);
        for (GuiControl control : scrollableTable)
            ((GuiRow) control).removeCol(index);
    }
    
    public Iterable<GuiRow> contentRows() {
        return new FunctionIterator<>(scrollableTable, x -> (GuiRow) x);
    }
    
    @Override
    public GuiControlDistHandler createDist(GuiControl control) {
        if (control == scrollableTable)
            return dist().createScrollBox(scrollableTable);
        if (control == topRow)
            return dist().createTopRow(topRow);
        if (control == firstCol)
            return dist().createFirstCol(firstCol);
        return super.createDist(control);
    }
    
    public static interface GuiTableScrollableDist extends GuiParentDistHandler {
        
        public GuiScrollXYDist createScrollBox(GuiScrollXY control);
        
        public GuiParentDistHandler createTopRow(GuiRow control);
        
        public GuiParentDistHandler createFirstCol(GuiParent control);
        
    }
    
}
