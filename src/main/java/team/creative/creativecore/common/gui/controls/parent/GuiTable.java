package team.creative.creativecore.common.gui.controls.parent;

import java.util.ArrayList;
import java.util.List;

import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.flow.GuiFlow;

public class GuiTable extends GuiParent {
    
    public GuiTable(String name) {
        super(name, GuiFlow.STACK_Y);
    }
    
    public GuiTable() {
        super(GuiFlow.STACK_Y);
    }
    
    public GuiTable(GuiRow... rows) {
        this();
        for (int i = 0; i < rows.length; i++)
            addRow(rows[i]);
    }
    
    public GuiTable addRow(GuiRow row) {
        super.add(row);
        return this;
    }
    
    @Override
    public GuiTable setExpandable() {
        return (GuiTable) super.setExpandable();
    }
    
    @Override
    public void flowX(int width, int preferred) {
        List<GuiTableGroup> cols = new ArrayList<>();
        for (GuiChildControl child : controls) {
            GuiRow row = (GuiRow) child.control;
            int i = 0;
            for (GuiChildControl cell : row) {
                if (cols.size() <= i)
                    cols.add(new GuiTableGroup());
                cols.get(i).controls.add(cell);
                i++;
            }
        }
        if (cols.isEmpty())
            return;
        GuiFlow.STACK_X.flowX(cols, spacing, Align.STRETCH, width, preferred, endlessX());
        
        if (cols.isEmpty())
            return;
        
        int startX = cols.get(0).getX();
        GuiChildControl lastCol = cols.get(cols.size() - 1);
        int combinedWidth = lastCol.getWidth() + lastCol.getX() - startX;
        for (GuiChildControl row : controls) {
            row.setX(startX);
            row.setWidth(combinedWidth, width);
        }
    }
    
    @Override
    @Deprecated
    public GuiChildControl add(GuiControl control) {
        throw new UnsupportedOperationException();
    }
    
    public static class GuiTableGroup extends GuiChildControl {
        
        public final List<GuiChildControl> controls = new ArrayList<>();
        
        public GuiTableGroup() {
            super(null);
        }
        
        @Override
        public int getMinWidth(int availableWidth) {
            int min = -1;
            for (GuiChildControl child : controls) {
                int minWidth = child.getMinWidth(availableWidth);
                if (minWidth != -1)
                    min = Math.max(min, minWidth);
            }
            return min;
        }
        
        @Override
        public int getMaxWidth(int availableWidth) {
            int max = -1;
            for (GuiChildControl child : controls) {
                int maxWidth = child.getMaxWidth(availableWidth);
                if (maxWidth != -1)
                    max = max == -1 ? maxWidth : Math.min(max, maxWidth);
            }
            return max;
        }
        
        @Override
        public int getPreferredWidth(int availableWidth) {
            int pref = -1;
            for (GuiChildControl child : controls)
                pref = Math.max(pref, child.getPreferredWidth(availableWidth));
            return pref;
        }
        
        @Override
        public int getMinHeight(int availableHeight) {
            int min = -1;
            for (GuiChildControl child : controls) {
                int minHeight = child.getMinHeight(availableHeight);
                if (minHeight != -1)
                    min = Math.max(min, minHeight);
            }
            return min;
        }
        
        @Override
        public int getMaxHeight(int availableHeight) {
            int max = -1;
            for (GuiChildControl child : controls) {
                int maxHeight = child.getMaxHeight(availableHeight);
                if (maxHeight != -1)
                    max = max == -1 ? maxHeight : Math.min(max, maxHeight);
            }
            return max;
        }
        
        @Override
        public int getPreferredHeight(int availableHeight) {
            int pref = -1;
            for (GuiChildControl child : controls)
                pref = Math.max(pref, child.getPreferredHeight(availableHeight));
            return pref;
        }
        
        @Override
        public int setWidth(int width, int availableWidth) {
            width = super.setWidth(width, availableWidth);
            for (GuiChildControl child : controls)
                child.setWidth(width, availableWidth);
            return width;
        }
        
        @Override
        public int setHeight(int height, int availableHeight) {
            height = super.setHeight(height, height);
            for (GuiChildControl child : controls)
                child.setHeight(height, height);
            return height;
        }
        
        @Override
        public void setX(int x) {
            super.setX(x);
            for (GuiChildControl child : controls)
                child.setX(x);
        }
        
        @Override
        public void setY(int y) {
            super.setY(y);
            for (GuiChildControl child : controls)
                child.setY(y);
        }
        
        @Override
        public void flowX() {
            for (GuiChildControl child : controls)
                child.flowX();
        }
        
        @Override
        public void flowY() {
            for (GuiChildControl child : controls)
                child.flowY();
        }
        
        @Override
        public boolean isExpandableX() {
            for (GuiChildControl child : controls)
                if (child.isExpandableX())
                    return true;
            return false;
        }
        
        @Override
        public boolean isExpandableY() {
            return false;
        }
        
    }
    
}
