package team.creative.creativecore.common.gui;

public interface IScaleableGuiScreen {
    
    public int getWidth();
    
    public int getHeight();
    
    public default int getMaxScale(int displayWidth, int displayHeight) {
        int scaleFactor = 1;
        while (scaleFactor < 100 && getWidth() * (scaleFactor + 1) <= displayWidth && getHeight() * (scaleFactor + 1) <= displayHeight)
            ++scaleFactor;
        return scaleFactor;
    }
    
    public void clientTick();
    
}
