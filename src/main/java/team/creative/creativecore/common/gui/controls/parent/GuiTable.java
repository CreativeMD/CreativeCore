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
    public void flowX(int width, int preferred) {
        super.flowX(width, preferred);
        List<GuiChildControl> cols = new ArrayList<>();
        for (GuiChildControl child : controls) {
            GuiRow row = (GuiRow) child.control;
            int i = 0;
            for (GuiChildControl cell : row) {
                if (cols.size() <= i)
                    cols.add(new GuiTableCol());
                ((GuiTableCol) cols.get(i)).controls.add(cell);
                i++;
            }
        }
        GuiFlow.STACK_X.flowX(cols, spacing, Align.STRETCH, width, preferred, endlessX());
    }
    
    @Override
    @Deprecated
    public GuiChildControl add(GuiControl control) {
        throw new UnsupportedOperationException();
    }
    
    private static class GuiTableCol extends GuiChildControl {
        
        public final List<GuiChildControl> controls = new ArrayList<>();
        
        public GuiTableCol() {
            super(null);
        }
        
        @Override
        public int getMinWidth(int availableWidth) {
            int min = -1;
            for (GuiChildControl child : controls)
                min = Math.max(min, child.getMinWidth(availableWidth));
            return min;
        }
        
        @Override
        public int getMaxWidth(int availableWidth) {
            return -1;
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
            throw new UnsupportedOperationException();
        }
        
        @Override
        public int getMaxHeight(int availableHeight) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public int getPreferredHeight(int availableHeight) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setWidth(int width, int availableWidth) {
            super.setWidth(width, availableWidth);
            for (GuiChildControl child : controls)
                child.setWidth(width, availableWidth);
        }
        
        @Override
        public void setHeight(int height, int availableHeight) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setX(int x) {
            super.setX(x);
            for (GuiChildControl child : controls)
                child.setX(x);
        }
        
        @Override
        public void setY(int y) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void flowX() {
            for (GuiChildControl child : controls)
                child.flowX();
        }
        
        @Override
        public void flowY() {
            throw new UnsupportedOperationException();
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
