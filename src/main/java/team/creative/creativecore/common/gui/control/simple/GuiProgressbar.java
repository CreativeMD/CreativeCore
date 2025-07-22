package team.creative.creativecore.common.gui.control.simple;

import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.parser.DoubleValueParser;

public class GuiProgressbar extends GuiControl {
    
    public double pos;
    public double max;
    public boolean showToolTip = true;
    public final DoubleValueParser parser;
    
    public GuiProgressbar(IGuiParent parent, String name, double pos, double max) {
        this(parent, name, pos, max, DoubleValueParser.PERCENT);
    }
    
    public GuiProgressbar(IGuiParent parent, String name, double pos, double max, DoubleValueParser valueParser) {
        super(parent, name);
        this.pos = pos;
        this.max = max;
        this.parser = valueParser;
    }
    
    @Override
    public void init() {}
    
    @Override
    public void closed() {}
    
    @Override
    public void tick() {}
    
    public double getPercentage() {
        return this.pos / this.max;
    }
    
}