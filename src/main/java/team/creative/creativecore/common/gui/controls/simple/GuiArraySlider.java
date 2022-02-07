package team.creative.creativecore.common.gui.controls.simple;

import org.apache.commons.lang3.ArrayUtils;

public class GuiArraySlider extends GuiSteppedSlider {
    
    public String[] values;
    
    public GuiArraySlider(String name, String value, String... values) {
        super(name, ArrayUtils.indexOf(values, value), 0, values.length - 1);
        this.values = values;
    }
    
    public GuiArraySlider(String name) {
        this(name, "", "");
    }
    
    public GuiArraySlider(String name, int width, int height, String value, String... values) {
        super(name, width, height, ArrayUtils.indexOf(values, value), 0, values.length - 1);
        this.values = values;
    }
    
    @Override
    public String getTextByValue() {
        if (value > values.length)
            return "";
        return values[(int) value] + "";
    }
    
    public void select(String value) {
        setValue(ArrayUtils.indexOf(values, value));
    }
    
    public void setValues(String[] values) {
        minValue = 0;
        maxValue = values.length - 1;
        this.values = values;
        setValue(0);
    }
    
    public String get() {
        return values[(int) value];
    }
}