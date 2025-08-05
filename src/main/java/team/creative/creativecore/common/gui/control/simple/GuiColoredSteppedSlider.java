package team.creative.creativecore.common.gui.control.simple;

import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.util.mc.ColorUtils.ColorPart;

public class GuiColoredSteppedSlider extends GuiSteppedSlider {
    
    public GuiColoredSteppedSlider(IGuiParent parent, String name, GuiColorPicker picker, ColorPart part) {
        super(parent, name, part.get(picker.color), 0, 255);
        if (dist() != null) {
            dist().setPicker(picker);
            dist().setPart(part);
        }
    }
    
    @Override
    public GuiColoredSteppedSliderDist dist() {
        return (GuiColoredSteppedSliderDist) super.dist();
    }
    
    public static interface GuiColoredSteppedSliderDist extends GuiSteppedSliderDist {
        
        public void setPicker(GuiColorPicker picker);
        
        public void setPart(ColorPart part);
        
    }
    
}