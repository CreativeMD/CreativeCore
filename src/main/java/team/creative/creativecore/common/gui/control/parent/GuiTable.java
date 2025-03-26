package team.creative.creativecore.common.gui.control.parent;

import java.util.ArrayList;
import java.util.List;

import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiControlRect;
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
        for (GuiControl control : controls) {
            int i = 0;
            for (GuiControl cell : (GuiRow) control) {
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
        GuiControlRect lastCol = cols.get(cols.size() - 1);
        int combinedWidth = lastCol.getWidth() + lastCol.getX() - startX;
        for (GuiControl row : controls) {
            row.rect.setX(startX);
            row.rect.setWidth(combinedWidth, width);
        }
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
    
    public static class GuiTableGroup extends GuiControlRect {
        
        public final List<GuiControl> controls = new ArrayList<>();
        
        public GuiTableGroup() {
            super(null);
        }
        
        @Override
        public int getMinWidth(int availableWidth) {
            int min = -1;
            for (GuiControl control : controls) {
                int minWidth = control.rect.getMinWidth(availableWidth);
                if (minWidth != -1)
                    min = Math.max(min, minWidth);
            }
            return min;
        }
        
        @Override
        public int getMaxWidth(int availableWidth) {
            int max = -1;
            for (GuiControl control : controls) {
                int maxWidth = control.rect.getMaxWidth(availableWidth);
                if (maxWidth != -1)
                    max = max == -1 ? maxWidth : Math.min(max, maxWidth);
            }
            return max;
        }
        
        @Override
        public int getPreferredWidth(int availableWidth) {
            int pref = -1;
            for (GuiControl control : controls)
                pref = Math.max(pref, GuiControlRect.getPreferredWidth(control, availableWidth));
            return pref;
        }
        
        @Override
        public int getMinHeight(int availableHeight) {
            int min = -1;
            for (GuiControl control : controls) {
                int minHeight = control.rect.getMinHeight(availableHeight);
                if (minHeight != -1)
                    min = Math.max(min, minHeight);
            }
            return min;
        }
        
        @Override
        public int getMaxHeight(int availableHeight) {
            int max = -1;
            for (GuiControl control : controls) {
                int maxHeight = control.rect.getMaxHeight(availableHeight);
                if (maxHeight != -1)
                    max = max == -1 ? maxHeight : Math.min(max, maxHeight);
            }
            return max;
        }
        
        @Override
        public int getPreferredHeight(int availableHeight) {
            int pref = -1;
            for (GuiControl control : controls)
                pref = Math.max(pref, control.rect.getPreferredHeight(availableHeight));
            return pref;
        }
        
        @Override
        public int setWidth(int width, int availableWidth) {
            width = super.setWidth(width, availableWidth);
            for (GuiControl control : controls)
                control.rect.setWidth(width, availableWidth);
            return width;
        }
        
        @Override
        public int setHeight(int height, int availableHeight) {
            height = super.setHeight(height, height);
            for (GuiControl control : controls)
                control.rect.setHeight(height, height);
            return height;
        }
        
        @Override
        public void setX(int x) {
            super.setX(x);
            for (GuiControl control : controls)
                control.rect.setX(x);
        }
        
        @Override
        public void setY(int y) {
            super.setY(y);
            for (GuiControl control : controls)
                control.rect.setY(y);
        }
        
        @Override
        public void flowX() {
            for (GuiControl control : controls)
                control.rect.flowX();
        }
        
        @Override
        public void flowY() {
            for (GuiControl control : controls)
                control.rect.flowY();
        }
        
        @Override
        public boolean isExpandableX() {
            for (GuiControl control : controls)
                if (control.isExpandableX())
                    return true;
            return false;
        }
        
        @Override
        public boolean isExpandableY() {
            return false;
        }
        
    }
    
}
