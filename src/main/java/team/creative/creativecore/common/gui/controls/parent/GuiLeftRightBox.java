package team.creative.creativecore.common.gui.controls.parent;

import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiControl;

public class GuiLeftRightBox extends GuiRow {
    
    private final GuiColumn left;
    private final GuiColumn right;
    
    public GuiLeftRightBox() {
        this.left = new GuiColumn();
        addColumn(left);
        this.right = new GuiColumn();
        this.right.align = Align.RIGHT;
        addColumn(right);
        setExpandableX();
    }
    
    public GuiLeftRightBox addLeft(GuiControl control) {
        left.add(control);
        return this;
    }
    
    public GuiLeftRightBox addRight(GuiControl control) {
        right.add(control);
        return this;
    }
    
}
