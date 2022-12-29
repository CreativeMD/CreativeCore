package team.creative.creativecore.common.gui.flow;

import java.util.List;

import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.VAlign;

public abstract class GuiFlow {
    
    public static final GuiStackX STACK_X = new GuiStackX();
    public static final GuiFlowFitX FIT_X = new GuiFlowFitX();
    public static final GuiStackY STACK_Y = new GuiStackY();
    
    public abstract int minWidth(List<GuiChildControl> controls, int spacing, int availableWidth);
    
    public abstract int preferredWidth(List<GuiChildControl> controls, int spacing, int availableWidth);
    
    public abstract int minHeight(List<GuiChildControl> controls, int spacing, int width, int availableWidth);
    
    public abstract int preferredHeight(List<GuiChildControl> controls, int spacing, int width, int availableWidth);
    
    public abstract void flowX(List<GuiChildControl> controls, int spacing, Align align, int width, int preferred);
    
    public abstract void flowY(List<GuiChildControl> controls, int spacing, VAlign valign, int width, int height, int preferred);
    
    public static boolean areChildrenExpandableX(List<GuiChildControl> controls) {
        for (GuiChildControl child : controls)
            if (child.control.isExpandableX())
                return true;
        return false;
    }
    
    public static boolean areChildrenExpandableY(List<GuiChildControl> controls) {
        for (GuiChildControl child : controls)
            if (child.control.isExpandableY())
                return true;
        return false;
    }
    
}
