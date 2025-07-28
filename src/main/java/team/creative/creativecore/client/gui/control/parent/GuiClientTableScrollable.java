package team.creative.creativecore.client.gui.control.parent;

import java.util.ArrayList;
import java.util.List;

import team.creative.creativecore.client.gui.GuiClientControl;
import team.creative.creativecore.client.gui.GuiClientParent;
import team.creative.creativecore.client.gui.GuiControlRect;
import team.creative.creativecore.client.gui.control.parent.GuiClientTable.GuiTableGroup;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.GuiParent.GuiParentDistHandler;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.control.parent.GuiRow;
import team.creative.creativecore.common.gui.control.parent.GuiScrollXY;
import team.creative.creativecore.common.gui.control.parent.GuiScrollXY.GuiScrollXYDist;
import team.creative.creativecore.common.gui.control.parent.GuiTableScrollable;
import team.creative.creativecore.common.gui.control.parent.GuiTableScrollable.GuiTableScrollableDist;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.gui.style.ControlFormatting.ControlStyleFace;

public class GuiClientTableScrollable<T extends GuiTableScrollable> extends GuiClientParent<T> implements GuiTableScrollableDist {
    
    public GuiClientTableScrollable(T control) {
        super(control);
    }
    
    @Override
    public GuiScrollXYDist createScrollBox(GuiScrollXY control) {
        var dist = new GuiClientScrollXY<GuiScrollXY>(control) {
            @Override
            public void flowX(int width, int preferred) {
                updateWidth();
            }
            
            @Override
            public void flowY(int width, int height, int preferred) {
                updateHeight();
            }
            
            @Override
            protected ControlFormatting defaultFormatting() {
                return ControlFormatting.TRANSPARENT;
            }
        };
        dist.scrollbarThickness = 2;
        dist.scrollbarFace = ControlStyleFace.CLICKABLE_INACTIVE;
        dist.alternativeScrolling = true;
        return dist;
    }
    
    @Override
    public GuiParentDistHandler createTopRow(GuiRow control) {
        return new GuiClientParent<GuiParent>(control) {
            
            @Override
            public double getOffsetX() {
                return ((GuiClientParent) GuiClientTableScrollable.this.control.scrollableTable.dist()).getOffsetX();
            }
            
        };
    }
    
    @Override
    public GuiParentDistHandler createFirstCol(GuiParent control) {
        return new GuiClientParent<GuiParent>(control) {
            
            @Override
            public double getOffsetY() {
                return ((GuiClientParent) GuiClientTableScrollable.this.control.scrollableTable.dist()).getOffsetY();
            }
            
        };
    }
    
    protected List<GuiTableGroup> createCols() {
        List<GuiTableGroup> cols = new ArrayList<>();
        
        int i = 0;
        for (GuiClientControl cell : ((GuiClientParent<?>) control.topRow.dist()).controls()) {
            if (cols.size() <= i)
                cols.add(new GuiTableGroup());
            cols.get(i).controls.add(cell);
            i++;
        }
        
        GuiTableGroup first = cols.get(0);
        for (GuiClientControl control : ((GuiClientParent<?>) control.firstCol.dist()).controls())
            first.controls.add(control);
        
        for (GuiClientControl control : ((GuiClientParent<?>) control.scrollableTable.dist()).controls()) {
            i = 1;
            for (GuiClientControl cell : ((GuiClientParent<?>) control).controls()) {
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
        
        GuiControlRect startCol = cols.get(0);
        int startX = startCol.getX();
        
        GuiControlRect lastCol = cols.get(cols.size() - 1);
        int combinedWidth = Math.min(width, lastCol.getWidth() + lastCol.getX() - startX);
        
        GuiControlRect bottomChild = ((GuiClientControl) control.bottom.dist()).rect;
        bottomChild.setX(0);
        bottomChild.setWidth(combinedWidth + startX, width);
        
        GuiControlRect firstColChild = ((GuiClientControl) control.firstCol.dist()).rect;
        firstColChild.setX(startX);
        firstColChild.setWidth(startCol.getWidth(), width);
        
        GuiControlRect topRowChild = ((GuiClientControl) control.topRow.dist()).rect;
        topRowChild.setX(startX);
        topRowChild.setWidth(combinedWidth, width);
        
        combinedWidth -= startCol.getWidth();
        
        GuiControlRect scrollableChild = ((GuiClientControl) control.scrollableTable.dist()).rect;
        scrollableChild.setX(startX + startCol.getWidth());
        scrollableChild.setWidth(combinedWidth, width);
        
        combinedWidth = lastCol.getWidth() + lastCol.getX() - startX - startCol.getWidth(); // Total might be bigger than width
        startX += startCol.getWidth();
        
        for (GuiClientControl row : (Iterable<GuiClientControl>) control.scrollableTable.dist().controls()) {
            row.rect.setX(0);
            row.rect.setWidth(combinedWidth, width);
            for (GuiClientControl col : ((GuiClientParent<?>) row).controls())
                col.rect.setX(col.rect.getX() - startX);
        }
        
        scrollableChild.flowX();
        
    }
    
    protected List<GuiControlRect> createRows() {
        List<GuiControlRect> rows = new ArrayList<>();
        rows.add(((GuiClientControl) control.topRow.dist()).rect);
        
        int i = 1;
        for (GuiClientControl row : ((GuiClientParent<?>) control.firstCol.dist()).controls()) {
            if (rows.size() <= i)
                rows.add(new GuiTableGroup());
            ((GuiTableGroup) rows.get(i)).controls.add(row);
            i++;
        }
        
        i = 1;
        for (GuiClientControl row : ((GuiClientParent<?>) control.scrollableTable.dist()).controls()) {
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
        GuiControlRect lastRow = rows.get(rows.size() - 1);
        
        for (int j = 1; j < rows.size(); j++)
            for (GuiClientControl child : ((GuiTableGroup) rows.get(j)).controls)
                child.rect.setY(child.rect.getY() - startY);
            
        int combinedHeight = Math.min(height - startY, lastRow.getHeight() + lastRow.getY() - startY);
        
        GuiControlRect bottomChild = ((GuiClientControl) control.bottom.dist()).rect;
        bottomChild.setY(startY);
        bottomChild.setHeight(combinedHeight, height);
        
        GuiControlRect firstColChild = ((GuiClientControl) control.firstCol.dist()).rect;
        firstColChild.setY(0);
        firstColChild.setHeight(combinedHeight, height);
        
        GuiControlRect scrollableChild = ((GuiClientControl) control.scrollableTable.dist()).rect;
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
    
}
