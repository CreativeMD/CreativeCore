package team.creative.creativecore.common.gui.control.simple;

import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.GuiControlDistHandler;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.manager.GuiManager;
import team.creative.creativecore.common.gui.manager.GuiManagerDist;
import team.creative.creativecore.common.gui.parser.DoubleValueParser;
import team.creative.creativecore.common.util.math.geo.Rect;

public class GuiSlider extends GuiControl implements IGuiParent {
    
    public GuiSlider(IGuiParent parent, String name, double value, double min, double max) {
        this(parent, name, value, min, max, DoubleValueParser.NONE);
    }
    
    public GuiSlider(IGuiParent parent, String name, double value, double min, double max, DoubleValueParser parser) {
        super(parent, name);
        if (dist() != null) {
            dist().setBounds(min, max, false);
            dist().setParser(parser);
            dist().setValue(value, false);
        }
    }
    
    @Override
    public GuiSliderDist dist() {
        return (GuiSliderDist) super.dist();
    }
    
    public String getTextByValue() {
        if (dist() != null)
            return dist().getTextByValue();
        return "";
    }
    
    public String getTextfieldValue() {
        if (dist() != null)
            return dist().getTextfieldValue();
        return "";
    }
    
    public double getPercentage() {
        if (dist() != null)
            return dist().getPercentage();
        return 0;
    }
    
    public void closeTextField() {
        if (dist() != null)
            dist().closeTextField();
    }
    
    public void setMaxValue(double maxValue) {
        if (dist() != null)
            dist().setMaxValue(maxValue);
    }
    
    public void setMinValue(double minValue) {
        if (dist() != null)
            dist().setMinValue(minValue);
    }
    
    public void setValue(double value) {
        if (dist() != null)
            dist().setValue(value, true);
    }
    
    public double getValue() {
        if (dist() != null)
            return dist().getValue();
        return 0;
    }
    
    public double getMinValue() {
        if (dist() != null)
            return dist().getMinValue();
        return 0;
    }
    
    public double getMaxValue() {
        if (dist() != null)
            return dist().getMaxValue();
        return 1;
    }
    
    public GuiSlider setSliderSize(int size) {
        if (dist() != null)
            dist().setSliderSize(size);
        return this;
    }
    
    public GuiSlider setMinSlider(GuiSlider slider) {
        if (dist() != null)
            dist().setMinSlider(slider);
        return this;
    }
    
    public GuiSlider setMaxSlider(GuiSlider slider) {
        if (dist() != null)
            dist().setMaxSlider(slider);
        return this;
    }
    
    @Override
    public boolean isContainer() {
        return this.getParent().isContainer();
    }
    
    @Override
    public void closeTopLayer() {
        this.getParent().closeTopLayer();
    }
    
    @Override
    public Rect toLayerRect(GuiControl control, Rect rect) {
        return this.getParent().toLayerRect(this, rect);
    }
    
    @Override
    public Rect toScreenRect(GuiControl control, Rect rect) {
        return this.getParent().toScreenRect(this, rect);
    }
    
    @Override
    public void init() {}
    
    @Override
    public void closed() {}
    
    @Override
    public void tick() {}
    
    @Override
    public void closeLayer(GuiLayer layer) {
        this.getParent().closeLayer(layer);;
    }
    
    @Override
    public GuiControlDistHandler createDist(GuiControl control) {
        return getParent().createDist(control);
    }
    
    @Override
    public <T extends GuiManagerDist> T createDist(GuiManager<T> manager) {
        return getParent().createDist(manager);
    }
    
    public static interface GuiSliderDist extends GuiControlDistHandler {
        
        public void setParser(DoubleValueParser parser);
        
        public String getTextByValue();
        
        public String getTextfieldValue();
        
        public double getPercentage();
        
        public void closeTextField();
        
        public void setBounds(double min, double max, boolean notify);
        
        public void setMaxValue(double maxValue);
        
        public void setMinValue(double minValue);
        
        public void setValue(double value, boolean notify);
        
        public double getValue();
        
        public double getMinValue();
        
        public double getMaxValue();
        
        public void setSliderSize(int size);
        
        public void setMinSlider(GuiSlider slider);
        
        public void setMaxSlider(GuiSlider slider);
    }
    
}
