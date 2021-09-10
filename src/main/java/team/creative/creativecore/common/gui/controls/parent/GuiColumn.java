package team.creative.creativecore.common.gui.controls.parent;

public class GuiColumn extends GuiBoxX {
    
    public GuiColumn() {
        super("");
    }
    
    public GuiColumn(int width) {
        super("");
        this.preferredWidth = width;
    }
    
    @Override
    public int preferredWidth() {
        if (preferredWidth != 0)
            return preferredWidth;
        return super.preferredWidth();
    }
    
}
