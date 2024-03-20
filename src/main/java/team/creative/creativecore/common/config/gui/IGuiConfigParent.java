package team.creative.creativecore.common.config.gui;

public interface IGuiConfigParent {
    
    public void setCustomData(Object object);
    
    public Object getCustomData();
    
    public void changed();
    
}
