package team.creative.creativecore.client.gui.control.simple;

import org.apache.commons.lang3.ArrayUtils;

import team.creative.creativecore.common.gui.control.simple.GuiArraySlider;
import team.creative.creativecore.common.gui.control.simple.GuiArraySlider.GuiArraySliderDist;

public class GuiClientArraySlider<T extends GuiArraySlider> extends GuiClientSteppedSlider<T> implements GuiArraySliderDist {
    
    public String[] values;
    
    public GuiClientArraySlider(T control) {
        super(control);
    }
    
    @Override
    public String getTextByValue() {
        if (this.getIntValue() > values.length)
            return "";
        return values[this.getIntValue()];
    }
    
    @Override
    public void select(String value) {
        this.setValue(ArrayUtils.indexOf(values, value));
    }
    
    @Override
    public void setValues(String[] values) {
        this.setMinValue(0);
        this.setMaxValue(values.length - 1);
        this.values = values;
        this.setValue(0);
    }
    
    @Override
    public String get() {
        return values[this.getIntValue()];
    }
    
}
