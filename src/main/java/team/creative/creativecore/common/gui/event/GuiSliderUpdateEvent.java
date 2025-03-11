package team.creative.creativecore.common.gui.event;

import team.creative.creativecore.common.gui.control.simple.GuiSlider;

public class GuiSliderUpdateEvent extends GuiControlChangedEvent<GuiSlider> {
    public GuiSliderUpdateEvent(GuiSlider control) {
        super(control);
    }
}
