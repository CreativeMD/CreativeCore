package team.creative.creativecore.common.gui.flow;

import java.util.List;

import team.creative.creativecore.client.gui.GuiControlRect;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.VAlign;

public abstract class GuiFlow {
    
    public static final GuiStackX STACK_X = new GuiStackX();
    public static final GuiFlowFitX FIT_X = new GuiFlowFitX();
    public static final GuiStackY STACK_Y = new GuiStackY();
    
    public static boolean areChildrenExpandableX(List<? extends GuiControlRect> controls) {
        for (GuiControlRect rect : controls)
            if (rect.isExpandableX())
                return true;
        return false;
    }
    
    public static boolean areChildrenExpandableY(List<? extends GuiControlRect> controls) {
        for (GuiControlRect rect : controls)
            if (rect.isExpandableY())
                return true;
        return false;
    }
    
    public abstract int minWidth(List<? extends GuiControlRect> controls, int spacing, int availableWidth);
    
    public abstract int preferredWidth(List<? extends GuiControlRect> controls, int spacing, int availableWidth);
    
    public abstract int minHeight(List<? extends GuiControlRect> controls, int spacing, int width, int availableWidth);
    
    public abstract int preferredHeight(List<? extends GuiControlRect> controls, int spacing, int width, int availableWidth);
    
    public abstract void flowX(List<? extends GuiControlRect> controls, int spacing, Align align, int width, int preferred, boolean endless);
    
    public abstract void flowY(List<? extends GuiControlRect> controls, int spacing, VAlign valign, int width, int height, int preferred, boolean endless);
    
}
