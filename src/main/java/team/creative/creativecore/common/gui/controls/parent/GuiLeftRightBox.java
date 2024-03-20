package team.creative.creativecore.common.gui.controls.parent;

import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.VAlign;

public class GuiLeftRightBox extends GuiRow {
    
    private final GuiColumn left;
    private final GuiColumn right;
    
    public GuiLeftRightBox() {
        this.left = (GuiColumn) new GuiColumn().setExpandableX();
        super.addColumn(left);
        this.right = new GuiColumn();
        this.right.align = Align.RIGHT;
        super.addColumn(right);
        setExpandableX();
    }
    
    @Override
    public boolean isExpandableX() {
        return expandableX;
    }
    
    public GuiLeftRightBox addLeft(GuiControl control) {
        left.add(control);
        return this;
    }
    
    public GuiLeftRightBox addRight(GuiControl control) {
        right.add(control);
        return this;
    }
    
    @Override
    public GuiLeftRightBox setVAlign(VAlign valign) {
        left.setVAlign(valign);
        right.setVAlign(valign);
        return this;
    }
    
    @Override
    @Deprecated
    public GuiRow addColumn(GuiColumn col) {
        throw new UnsupportedOperationException();
    }
    
}
