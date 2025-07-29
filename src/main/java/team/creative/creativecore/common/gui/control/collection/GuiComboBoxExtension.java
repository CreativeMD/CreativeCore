package team.creative.creativecore.common.gui.control.collection;

import java.util.ArrayList;

import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.control.simple.GuiListEntry;

public class GuiComboBoxExtension extends GuiListBoxBase<GuiListEntry> {
    
    public GuiComboBoxExtension(IGuiParent parent, String name) {
        super(parent, name, false, new ArrayList<>());
        dist().init();
    }
    
    @Override
    public GuiComboBoxExtensionDist dist() {
        return (GuiComboBoxExtensionDist) super.dist();
    }
    
    public static interface GuiComboBoxExtensionDist extends GuiListBoxBaseDist {
        
        public void init();
        
    }
    
}
