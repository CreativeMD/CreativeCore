package com.creativemd.creativecore.common.gui.controls.gui;

import org.apache.commons.lang3.ArrayUtils;

public class GuiArraySlider extends GuiSteppedSlider {
    
    public String[] values;
    
    public GuiArraySlider(String name, int x, int y, int width, int height, String value, String... values) {
        super(name, x, y, width, height, ArrayUtils.indexOf(values, value), 0, values.length - 1);
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
    
    public String getValue() {
        return values[(int) value];
    }
}
