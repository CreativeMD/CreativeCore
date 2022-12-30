package team.creative.creativecore.common.gui;

public interface IScaleableGuiScreen {
    
    public int getWidth();
    
    public int getHeight();
    
    public default int getMaxScale(int displayWidth, int displayHeight) {
        int scaleFactor = 1;
        int width = getWidth();
        int height = getHeight();
        while (scaleFactor < 100 && width * (scaleFactor + 1) <= displayWidth && height * (scaleFactor + 1) <= displayHeight)
            ++scaleFactor;
        return scaleFactor;
    }
    
    public void clientTick();
    
}
