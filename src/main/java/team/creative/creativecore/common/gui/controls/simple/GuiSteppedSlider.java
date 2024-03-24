package team.creative.creativecore.common.gui.controls.simple;

import net.minecraft.client.gui.screens.Screen;
import team.creative.creativecore.common.gui.parser.IntValueParser;
import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiSteppedSlider extends GuiSlider {

    private final IntValueParser steppedParser;
    public GuiSteppedSlider(String name, int value, int min, int max) {
        this(name, value, min, max, IntValueParser.NONE);
    }
    
    public GuiSteppedSlider(String name, int value, int min, int max, IntValueParser parser) {
        super(name, value, min, max);
        this.steppedParser = parser;
    }
    
    @Override
    public String getTextByValue() {
        return steppedParser.parse(this.getIntValue(), this.getIntMaxValue());
    }
    
    @Override
    public String getTextfieldValue() {
        return this.getTextByValue();
    }
    
    public void stepUp() {
        setValue(getValue() + 1);
    }
    
    public void stepDown() {
        setValue(getValue() - 1);
    }
    
    @Override
    public boolean mouseScrolled(Rect rect, double x, double y, double scrolled) {
        this.setValue(getIntValue() + (Screen.hasShiftDown() ? 10 : 1) * (scrolled > 0 ? 1 : -1));
        return true;
    }
    
    @Override
    public void mouseMoved(Rect rect, double x, double y) {
        super.mouseMoved(rect, x, y);
        value = (int) value;
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
        return (int) value;
    }

    public int getIntMaxValue() {
        return (int) maxValue;
    }

    public int getIntMinValue() {
        return (int) minValue;
    }
    
    @Override
    protected GuiTextfield createTextfield(Rect rect) {
        return super.createTextfield(rect).setNumbersIncludingNegativeOnly();
    }

}
