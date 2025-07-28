package team.creative.creativecore.common.gui.control.parent;

import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.flow.GuiFlow;

public class GuiTable extends GuiParent {
    
    public GuiTable(IGuiParent parent, String name) {
        super(parent, name, GuiFlow.STACK_Y);
    }
    
    public GuiTable(IGuiParent parent) {
        super(parent, GuiFlow.STACK_Y);
    }
    
    public GuiTable(IGuiParent parent, GuiRow... rows) {
        this(parent);
        for (int i = 0; i < rows.length; i++)
            addRow(rows[i]);
    }
    
    public GuiTable addRow(GuiRow row) {
        super.add(row);
        return this;
    }
    
    @Override
    public GuiTable setExpandable() {
        return (GuiTable) super.setExpandable();
    }
    
    @Override
    @Deprecated
    public GuiParent add(GuiControl control) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    @Deprecated
    public GuiParent addHover(GuiControl control) {
        throw new UnsupportedOperationException();
    }
}
