package team.creative.creativecore.common.gui.controls.parent;

import java.util.ArrayList;
import java.util.List;

import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.controls.parent.GuiTable.GuiTableGroup;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleFace;
import team.creative.creativecore.common.util.type.itr.FunctionIterator;

public class GuiTableScrollable extends GuiParent {
    
    protected GuiRow topRow = new GuiRow() {
        @Override
        public double getOffsetX() {
            return scrollableTable.getOffsetX();
        }
    };
    protected GuiParent bottom = new GuiParent();
    protected GuiParent firstCol = new GuiParent(GuiFlow.STACK_Y) {
        @Override
        public double getOffsetY() {
            return scrollableTable.getOffsetY();
        }
    };
    protected GuiScrollXY scrollableTable = new GuiScrollXY("", GuiFlow.STACK_Y) {
        @Override
        public void flowX(int width, int preferred) {
            updateWidth();
        }
        
        @Override
        public void flowY(int width, int height, int preferred) {
            updateHeight();
        }
        
        @Override
        public ControlFormatting getControlFormatting() {
            return ControlFormatting.TRANSPARENT;
        }
    };
    
    public GuiTableScrollable() {
        this("");
    }
    
    public GuiTableScrollable(String name) {
        super(name);
        scrollableTable.scrollbarThickness = 2;
        scrollableTable.scrollbarFace = ControlStyleFace.CLICKABLE_INACTIVE;
        scrollableTable.alternativeScrolling = true;
        
        this.flow = GuiFlow.STACK_Y;
        super.add(topRow);
        super.add(bottom);
        bottom.add(firstCol);
        bottom.add(scrollableTable);
        spacing = 4;
    }
    
    @Override
    @Deprecated
    public GuiChildControl add(GuiControl control) {
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
        return expandableX;
    }
    
    @Override
    public boolean isExpandableY() {
        return expandableY;
    }
    
    protected List<GuiTableGroup> createCols() {
        List<GuiTableGroup> cols = new ArrayList<>();
        
        int i = 0;
        for (GuiChildControl cell : topRow) {
            if (cols.size() <= i)
                cols.add(new GuiTableGroup());
            cols.get(i).controls.add(cell);
            i++;
        }
        
        GuiTableGroup first = cols.get(0);
        for (GuiChildControl child : firstCol)
            first.controls.add(child);
        
        for (GuiChildControl child : scrollableTable) {
            GuiRow row = (GuiRow) child.control;
            i = 1;
            for (GuiChildControl cell : row) {
                if (cols.size() <= i)
                    cols.add(new GuiTableGroup());
                cols.get(i).controls.add(cell);
                i++;
            }
        }
        return cols;
    }
    
    @Override
    protected int preferredWidth(int availableWidth) {
        return GuiFlow.STACK_X.preferredWidth(createCols(), spacing, availableWidth);
    }
    
    @Override
    public void flowX(int width, int preferred) {
        var cols = createCols();
        GuiFlow.STACK_X.flowX(cols, spacing, Align.LEFT, width, preferred, endlessX());
        
        GuiChildControl startCol = cols.get(0);
        int startX = startCol.getX();
        
        GuiChildControl lastCol = cols.get(cols.size() - 1);
        int combinedWidth = Math.min(width, lastCol.getWidth() + lastCol.getX() - startX);
        
        GuiChildControl bottomChild = find(bottom);
        bottomChild.setX(0);
        bottomChild.setWidth(combinedWidth + startX, width);
        
        GuiChildControl firstColChild = bottom.find(firstCol);
        firstColChild.setX(startX);
        firstColChild.setWidth(startCol.getWidth(), width);
        
        GuiChildControl topRowChild = find(topRow);
        topRowChild.setX(startX);
        topRowChild.setWidth(combinedWidth, width);
        
        combinedWidth -= startCol.getWidth();
        
        GuiChildControl scrollableChild = bottom.find(scrollableTable);
        scrollableChild.setX(startX + startCol.getWidth());
        scrollableChild.setWidth(combinedWidth, width);
        
        combinedWidth = lastCol.getWidth() + lastCol.getX() - startX - startCol.getWidth(); // Total might be bigger than width
        startX += startCol.getWidth();
        
        for (GuiChildControl row : scrollableTable) {
            row.setX(0);
            row.setWidth(combinedWidth, width);
            for (GuiChildControl col : ((GuiRow) row.control))
                col.setX(col.getX() - startX);
        }
        
        scrollableChild.flowX();
        
    }
    
    protected List<GuiChildControl> createRows() {
        List<GuiChildControl> rows = new ArrayList<>();
        rows.add(find(topRow));
        
        int i = 1;
        for (GuiChildControl row : firstCol) {
            if (rows.size() <= i)
                rows.add(new GuiTableGroup());
            ((GuiTableGroup) rows.get(i)).controls.add(row);
            i++;
        }
        
        i = 1;
        for (GuiChildControl row : scrollableTable) {
            if (rows.size() <= i)
                rows.add(new GuiTableGroup());
            ((GuiTableGroup) rows.get(i)).controls.add(row);
            i++;
        }
        return rows;
    }
    
    @Override
    protected int preferredHeight(int width, int availableHeight) {
        return GuiFlow.STACK_Y.preferredHeight(createRows(), spacing, width, availableHeight);
    }
    
    @Override
    public void flowY(int width, int height, int preferred) {
        var rows = createRows();
        
        GuiFlow.STACK_Y.flowY(rows, spacing, VAlign.TOP, width, height, preferred, endlessY());
        
        int startY = rows.size() > 1 ? rows.get(1).getY() : rows.get(0).getHeight() + rows.get(0).getY();
        GuiChildControl lastRow = rows.get(rows.size() - 1);
        
        for (int j = 1; j < rows.size(); j++)
            for (GuiChildControl child : ((GuiTableGroup) rows.get(j)).controls)
                child.setY(child.getY() - startY);
            
        int combinedHeight = Math.min(height - startY, lastRow.getHeight() + lastRow.getY() - startY);
        
        GuiChildControl bottomChild = find(bottom);
        bottomChild.setY(startY);
        bottomChild.setHeight(combinedHeight, height);
        
        GuiChildControl firstColChild = bottom.find(firstCol);
        firstColChild.setY(0);
        firstColChild.setHeight(combinedHeight, height);
        
        GuiChildControl scrollableChild = bottom.find(scrollableTable);
        scrollableChild.setY(0);
        scrollableChild.setHeight(combinedHeight, height);
        
        scrollableChild.flowY();
        
    }
    
    @Override
    protected boolean endlessX() {
        return true;
    }
    
    @Override
    protected boolean endlessY() {
        return true;
    }
    
    public void removeContentCol(int index) {
        topRow.removeCol(index + 1);
        for (GuiChildControl child : scrollableTable)
            ((GuiRow) child.control).removeCol(index);
    }
    
    public Iterable<GuiRow> contentRows() {
        return new FunctionIterator<>(scrollableTable, x -> (GuiRow) x.control);
    }
    
}
