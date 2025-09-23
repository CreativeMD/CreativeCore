package team.creative.creativecore.common.gui.control.simple;

import org.apache.commons.lang3.ArrayUtils;

import team.creative.creativecore.common.gui.IGuiParent;

public class GuiArraySlider extends GuiSteppedSlider {
    
    public GuiArraySlider(IGuiParent parent, String name, String value, String... values) {
        super(parent, name, ArrayUtils.indexOf(values, value), 0, values.length - 1);
        if (dist() != null)
            dist().setValues(values, false);
    }
    
    public GuiArraySlider(IGuiParent parent, String name) {
        this(parent, name, "", "");
    }
    
    @Override
    public GuiArraySliderDist dist() {
        return (GuiArraySliderDist) super.dist();
    }
    
    public void select(String value) {
        if (dist() != null)
            dist().select(value, true);
    }
    
    public void setValues(String[] values) {
        if (dist() != null)
            dist().setValues(values, true);
    }
    
    public String get() {
        if (dist() != null)
            return dist().get();
        return "";
    }
    
    public static interface GuiArraySliderDist extends GuiSteppedSliderDist {
        
        public void select(String value, boolean notify);
        
        public void setValues(String[] values, boolean notify);
        
        public String get();
        
    }
}