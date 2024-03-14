package team.creative.creativecore.common.gui.flow;

import java.util.List;

import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.VAlign;

public abstract class GuiFlow {
    
    public static final GuiStackX STACK_X = new GuiStackX();
    public static final GuiFlowFitX FIT_X = new GuiFlowFitX();
    public static final GuiStackY STACK_Y = new GuiStackY();
    
    public abstract int minWidth(List<? extends GuiChildControl> controls, int spacing, int availableWidth);
    
    public abstract int preferredWidth(List<? extends GuiChildControl> controls, int spacing, int availableWidth);
    
    public abstract int minHeight(List<? extends GuiChildControl> controls, int spacing, int width, int availableWidth);
    
    public abstract int preferredHeight(List<? extends GuiChildControl> controls, int spacing, int width, int availableWidth);
    
    public abstract void flowX(List<? extends GuiChildControl> controls, int spacing, Align align, int width, int preferred, boolean endless);
    
    public abstract void flowY(List<? extends GuiChildControl> controls, int spacing, VAlign valign, int width, int height, int preferred, boolean endless);
    
    public static boolean areChildrenExpandableX(List<? extends GuiChildControl> controls) {
        for (GuiChildControl child : controls)
            if (child.isExpandableX())
                return true;
        return false;
    }
    
    public static boolean areChildrenExpandableY(List<? extends GuiChildControl> controls) {
        for (GuiChildControl child : controls)
            if (child.isExpandableY())
                return true;
        return false;
    }
    
}
