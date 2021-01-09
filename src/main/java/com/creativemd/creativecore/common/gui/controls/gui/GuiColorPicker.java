package com.creativemd.creativecore.common.gui.controls.gui;

import org.lwjgl.util.Color;

import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.container.GuiParent;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import com.creativemd.creativecore.common.utils.mc.ColorUtils.ColorPart;

public class GuiColorPicker extends GuiParent {
    
    public Color color;
    
    public GuiColorPicker(String name, int x, int y, Color color, boolean hasAlpha, int alphaMin) {
        super(name, x, y, 140, hasAlpha ? 40 : 30);
        marginWidth = 0;
        this.color = color;
        setStyle(Style.emptyStyle);
        
        addControl(new GuiButtonHold("r-", "<", 0, 0, 1, 5) {
            
            @Override
            public void onClicked(int x, int y, int button) {
                onColorChanged();
                GuiColoredSteppedSlider slider = (GuiColoredSteppedSlider) get("r");
                slider.setValue(slider.value - 1);
            }
            
        });
        addControl(new GuiButtonHold("r+", ">", 98, 0, 1, 5) {
            
            @Override
            public void onClicked(int x, int y, int button) {
                onColorChanged();
                GuiColoredSteppedSlider slider = (GuiColoredSteppedSlider) get("r");
                slider.setValue(slider.value + 1);
            }
            
        });
        
        addControl(new GuiButtonHold("g-", "<", 0, 10, 1, 5) {
            
            @Override
            public void onClicked(int x, int y, int button) {
                onColorChanged();
                GuiColoredSteppedSlider slider = (GuiColoredSteppedSlider) get("g");
                slider.setValue(slider.value - 1);
            }
            
        });
        addControl(new GuiButtonHold("g+", ">", 98, 10, 1, 5) {
            
            @Override
            public void onClicked(int x, int y, int button) {
                onColorChanged();
                GuiColoredSteppedSlider slider = (GuiColoredSteppedSlider) get("g");
                slider.setValue(slider.value + 1);
            }
            
        });
        
        addControl(new GuiButtonHold("b-", "<", 0, 20, 1, 5) {
            
            @Override
            public void onClicked(int x, int y, int button) {
                onColorChanged();
                GuiColoredSteppedSlider slider = (GuiColoredSteppedSlider) get("b");
                slider.setValue(slider.value - 1);
            }
            
        });
        addControl(new GuiButtonHold("b+", ">", 98, 20, 1, 5) {
            
            @Override
            public void onClicked(int x, int y, int button) {
                onColorChanged();
                GuiColoredSteppedSlider slider = (GuiColoredSteppedSlider) get("b");
                slider.setValue(slider.value + 1);
            }
            
        });
        
        if (hasAlpha) {
            
            addControl(new GuiButtonHold("a-", "<", 0, 30, 1, 5) {
                
                @Override
                public void onClicked(int x, int y, int button) {
                    onColorChanged();
                    GuiColoredSteppedSlider slider = (GuiColoredSteppedSlider) get("a");
                    slider.setValue(slider.value - 1);
                }
                
            });
            addControl(new GuiButtonHold("a+", ">", 98, 30, 1, 5) {
                
                @Override
                public void onClicked(int x, int y, int button) {
                    onColorChanged();
                    GuiColoredSteppedSlider slider = (GuiColoredSteppedSlider) get("a");
                    slider.setValue(slider.value + 1);
                }
                
            });
        } else
            color.setAlpha(255);
        
        addControl(new GuiColoredSteppedSlider("r", 8, 0, 84, 5, this, ColorPart.RED).setStyle(defaultStyle));
        addControl(new GuiColoredSteppedSlider("g", 8, 10, 84, 5, this, ColorPart.GREEN).setStyle(defaultStyle));
        addControl(new GuiColoredSteppedSlider("b", 8, 20, 84, 5, this, ColorPart.BLUE).setStyle(defaultStyle));
        if (hasAlpha) {
            GuiColoredSteppedSlider alpha = new GuiColoredSteppedSlider("a", 8, 30, 84, 5, this, ColorPart.ALPHA);
            alpha.minValue = alphaMin;
            addControl(alpha.setStyle(defaultStyle));
        }
        addControl(new GuiColorPlate("plate", 107, 2, 20, 20, color).setStyle(defaultStyle));
        
    }
    
    public void setColor(Color color) {
        this.color.setColor(color);
        ((GuiColoredSteppedSlider) get("r")).value = color.getRed();
        ((GuiColoredSteppedSlider) get("g")).value = color.getGreen();
        ((GuiColoredSteppedSlider) get("b")).value = color.getBlue();
        ((GuiColoredSteppedSlider) get("a")).value = color.getAlpha();
        
    }
    
    public void onColorChanged() {
        raiseEvent(new GuiControlChangedEvent(this));
    }
    
}
