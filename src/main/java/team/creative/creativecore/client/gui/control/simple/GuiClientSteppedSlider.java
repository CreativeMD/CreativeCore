package team.creative.creativecore.client.gui.control.simple;

import net.minecraft.client.gui.screens.Screen;
import team.creative.creativecore.common.gui.control.simple.GuiSteppedSlider;
import team.creative.creativecore.common.gui.control.simple.GuiSteppedSlider.GuiSteppedSliderDist;
import team.creative.creativecore.common.gui.control.simple.GuiTextfield;
import team.creative.creativecore.common.gui.parser.IntValueParser;

public class GuiClientSteppedSlider<T extends GuiSteppedSlider> extends GuiClientSlider<T> implements GuiSteppedSliderDist {
    
    private IntValueParser steppedParser;
    
    public GuiClientSteppedSlider(T control) {
        super(control);
    }
    
    @Override
    public void setSteppedParser(IntValueParser steppedParser) {
        this.steppedParser = steppedParser;
    }
    
    @Override
    public boolean mouseScrolled(double x, double y, double scrolled) {
        this.setValue(getIntValue() + (Screen.hasShiftDown() ? 10 : 1) * (scrolled > 0 ? 1 : -1), true);
        return true;
    }
    
    @Override
    public String getTextByValue() {
        return steppedParser.parse(this.getIntValue(), this.getIntMaxValue());
    }
    
    @Override
    public String getTextfieldValue() {
        return this.getTextByValue();
    }
    
    @Override
    public void mouseMoved(double x, double y) {
        super.mouseMoved(x, y);
        value = (int) value;
    }
    
    @Override
    protected GuiTextfield createTextfield() {
        return super.createTextfield().setNumbersIncludingNegativeOnly();
    }
    
    @Override
    public int getIntValue() {
        return (int) getValue();
    }
    
    @Override
    public int getIntMaxValue() {
        return (int) getMaxValue();
    }
    
    @Override
    public int getIntMinValue() {
        return (int) getMinValue();
    }
    
}
