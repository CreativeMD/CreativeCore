package team.creative.creativecore.common.gui.flow;

import java.util.ArrayList;
import java.util.List;

import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.VAlign;

public class GuiFlowFitX extends GuiStackX {
    
    @Override
    public int minHeight(List<GuiChildControl> controls, int spacing, int width) {
        int line = 0;
        int lineHeight = 0;
        int total = 0;
        for (GuiChildControl child : controls) {
            if (child.getY() == line)
                lineHeight = Math.max(lineHeight, child.getMaxHeight());
            else {
                line = child.getY();
                total += lineHeight;
                lineHeight = child.getMaxHeight();
            }
        }
        return total;
    }
    
    @Override
    public int preferredHeight(List<GuiChildControl> controls, int spacing, int width) {
        int line = 0;
        int lineHeight = 0;
        int total = 0;
        for (GuiChildControl child : controls) {
            if (child.getY() == line)
                lineHeight = Math.max(lineHeight, child.getPreferredHeight());
            else {
                line = child.getY();
                total += lineHeight;
                lineHeight = child.getPreferredHeight();
            }
        }
        return total;
    }
    
    @Override
    public void flowX(List<GuiChildControl> controls, int spacing, Align align, int width, int preferred) {
        int rowIndex = 0;
        int x = 0;
        List<GuiChildControl> row = new ArrayList<>();
        for (int i = 0; i < controls.size(); i++) {
            GuiChildControl child = controls.get(i);
            int pref = child.getPreferredWidth();
            if (width - x >= pref) {
                child.setY(rowIndex);
                row.add(child);
                x += pref;
            } else {
                super.flowX(row, spacing, align, width, preferred);
                row.clear();
                rowIndex++;
                child.setY(rowIndex);
                row.add(child);
                x = 0;
            }
        }
        if (!row.isEmpty())
            super.flowX(row, spacing, align, width, preferred);
    }
    
    @Override
    public void flowY(List<GuiChildControl> controls, int spacing, VAlign valign, int width, int height, int preferred) {
        List<GuiChildControl> rows = new ArrayList<>();
        List<GuiChildControl> row = new ArrayList<>();
        int line = 0;
        for (GuiChildControl child : controls) {
            if (child.getY() == line)
                row.add(child);
            else {
                rows.add(new GuiRowControl(new ArrayList<>(row), spacing, valign, width));
                line = child.getY();
                row.clear();
            }
        }
        
        GuiFlow.STACK_Y.flowY(rows, spacing, valign, width, height, preferred);
    }
    
    public static class GuiRowControl extends GuiChildControl {
        
        final List<GuiChildControl> controls;
        final int spacing;
        final int width;
        final VAlign valign;
        
        public GuiRowControl(List<GuiChildControl> controls, int spacing, VAlign valign, int width) {
            super(null);
            this.controls = controls;
            this.spacing = spacing;
            this.valign = valign;
            this.width = width;
        }
        
        @Override
        public int getContentWidth() {
            return getWidth();
        }
        
        @Override
        public int getMinWidth() {
            return GuiFlow.STACK_X.minWidth(controls, spacing);
        }
        
        @Override
        public int getMaxWidth() {
            return -1;
        }
        
        @Override
        public int getPreferredWidth() {
            return GuiFlow.STACK_X.preferredWidth(controls, spacing);
        }
        
        @Override
        public int getContentHeight() {
            return getHeight();
        }
        
        @Override
        public int getMinHeight() {
            return GuiFlow.STACK_X.minHeight(controls, spacing, width);
        }
        
        @Override
        public int getMaxHeight() {
            return -1;
        }
        
        @Override
        public int getPreferredHeight() {
            return GuiFlow.STACK_X.preferredHeight(controls, spacing, width);
        }
        
        @Override
        public void flowX() {}
        
        @Override
        public void flowY() {
            GuiFlow.STACK_X.flowY(controls, spacing, valign, width, getHeight(), getPreferredHeight());
        }
        
    }
    
}
