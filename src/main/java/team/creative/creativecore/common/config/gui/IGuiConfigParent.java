package team.creative.creativecore.common.config.gui;

import net.minecraft.core.HolderLookup;

public interface IGuiConfigParent {
    
    public HolderLookup.Provider provider();
    
    public void setCustomData(Object object);
    
    public Object getCustomData();
    
    public void changed();
    
}
