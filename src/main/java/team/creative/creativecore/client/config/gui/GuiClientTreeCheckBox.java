package team.creative.creativecore.client.config.gui;

import net.minecraft.sounds.SoundEvents;
import team.creative.creativecore.client.gui.control.simple.GuiClientCheckBox;
import team.creative.creativecore.common.config.gui.ClientSyncGuiLayer.GuiTreeCheckBox;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;

public class GuiClientTreeCheckBox<T extends GuiTreeCheckBox> extends GuiClientCheckBox<T> {
    
    public GuiClientTreeCheckBox(T control) {
        super(control);
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        playSound(SoundEvents.UI_BUTTON_CLICK);
        this.value = !value;
        
        if (value)
            control.entry.enable();
        else
            control.entry.disable();
        raiseEvent(new GuiControlChangedEvent(control));
        return true;
    }
    
}
