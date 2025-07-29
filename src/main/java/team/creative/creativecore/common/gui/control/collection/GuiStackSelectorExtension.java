package team.creative.creativecore.common.gui.control.collection;

import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.control.parent.GuiScrollY;
import team.creative.creativecore.common.gui.flow.GuiSizeRule;

public class GuiStackSelectorExtension extends GuiScrollY {
    
    public GuiStackSelectorExtension(IGuiParent parent, String name) {
        super(parent, name);
        setDim(new GuiSizeRule.GuiSizeRules().maxHeight(100));
    }
    
    @Override
    public GuiStackSelectorExtensionDist dist() {
        return (GuiStackSelectorExtensionDist) super.dist();
    }
    
    public static interface GuiStackSelectorExtensionDist extends GuiScrollYDist {
        
        public void reflowInternal();
        
    }
}
