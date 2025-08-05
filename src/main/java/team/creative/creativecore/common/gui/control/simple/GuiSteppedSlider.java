package team.creative.creativecore.common.gui.control.simple;

import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.parser.IntValueParser;

public class GuiSteppedSlider extends GuiSlider {
    
    public GuiSteppedSlider(IGuiParent parent, String name, int value, int min, int max) {
        this(parent, name, value, min, max, IntValueParser.NONE);
    }
    
    public GuiSteppedSlider(IGuiParent parent, String name, int value, int min, int max, IntValueParser parser) {
        super(parent, name, value, min, max);
        if (dist() != null)
            dist().setSteppedParser(parser);
    }
    
    @Override
    public GuiSteppedSliderDist dist() {
        return (GuiSteppedSliderDist) super.dist();
    }
    
    public void stepUp() {
        setValue(getValue() + 1);
    }
    
    public void stepDown() {
        setValue(getValue() - 1);
    }
    
    @Override
    public void setValue(double value) {
        super.setValue((int) value);
    }
    
    @Override
    public void setMaxValue(double maxValue) {
        super.setMaxValue((int) maxValue);
    }
    
    @Override
    public void setMinValue(double minValue) {
        super.setMinValue((int) minValue);
    }
    
    @Override
    public double getValue() {
        return (int) super.getValue();
    }
    
    @Override
    public double getMinValue() {
        return (int) super.getMinValue();
    }
    
    @Override
    public double getMaxValue() {
        return (int) super.getMaxValue();
    }
    
    public int getIntValue() {
        if (dist() != null)
            return dist().getIntValue();
        return 0;
    }
    
    public int getIntMaxValue() {
        if (dist() != null)
            return dist().getIntMaxValue();
        return 1;
    }
    
    public int getIntMinValue() {
        if (dist() != null)
            return dist().getIntMinValue();
        return 0;
    }
    
    public static interface GuiSteppedSliderDist extends GuiSliderDist {
        
        public void setSteppedParser(IntValueParser steppedParser);
        
        public int getIntValue();
        
        public int getIntMaxValue();
        
        public int getIntMinValue();
    }
    
}
