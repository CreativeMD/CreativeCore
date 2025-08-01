package team.creative.creativecore.common.config.gui;

import net.minecraft.core.HolderLookup;
import team.creative.creativecore.common.gui.IGuiParent;

public interface IGuiConfigParent extends IGuiParent {
    
    public HolderLookup.Provider provider();
    
    public void setCustomData(Object object);
    
    public Object getCustomData();
    
    public void changed();
    
}
