package team.creative.creativecore.common.gui.event;

import team.creative.creativecore.common.gui.control.simple.GuiTextfield;

public class GuiTextUpdateEvent extends GuiControlChangedEvent<GuiTextfield> {

    public GuiTextUpdateEvent(GuiTextfield control) {
        super(control);
    }
}
