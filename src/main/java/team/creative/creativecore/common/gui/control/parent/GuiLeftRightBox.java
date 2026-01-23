package team.creative.creativecore.common.gui.control.parent;

import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.VAlign;

public class GuiLeftRightBox extends GuiRow {
    
    private final GuiColumn left;
    private final GuiColumn right;
    
    public GuiLeftRightBox(IGuiParent parent) {
        super(parent);
        this.left = (GuiColumn) new GuiColumn(this).setExpandableX();
        super.addColumn(left);
        this.right = new GuiColumn(this);
        this.right.setAlign(Align.RIGHT);
        super.addColumn(right);
        setExpandableX();
    }
    
    @Override
    public boolean isExpandableX() {
        if (dist() != null)
            return dist().isExpandableX();
        return false;
    }
    
    public GuiColumn getLeft() {
        return left;
    }
    
    public GuiColumn getRight() {
        return right;
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
        if (left != null) {
            left.setVAlign(valign);
            right.setVAlign(valign);
        }
        return this;
    }
    
    @Override
    @Deprecated
    public GuiRow addColumn(GuiColumn col) {
        throw new UnsupportedOperationException();
    }
    
}
