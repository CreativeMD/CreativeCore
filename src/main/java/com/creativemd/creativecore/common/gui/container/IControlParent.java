package com.creativemd.creativecore.common.gui.container;

import java.util.List;

import com.creativemd.creativecore.common.gui.CoreControl;

import net.minecraft.entity.player.EntityPlayer;

public interface IControlParent {
    
    public List getControls();
    
    public void refreshControls();
    
    public CoreControl get(String name);
    
    public boolean has(String name);
    
    public EntityPlayer getPlayer();
    
    public default void removeControls(String... exclude) {
        List controls = getControls();
        int i = 0;
        while (i < controls.size()) {
            boolean canBeDeleted = true;
            if (controls.get(i) instanceof CoreControl)
                for (int j = 0; j < exclude.length; j++)
                    if (((CoreControl) controls.get(i)).name.contains(exclude[j])) {
                        canBeDeleted = false;
                        break;
                    }
            if (canBeDeleted)
                controls.remove(i);
            else
                i++;
        }
    }
    
    public default void moveControlBehind(CoreControl control, CoreControl controlInBack) {
        List controls = getControls();
        if (controls.contains(controlInBack) && controls.remove(control) && controls.indexOf(controlInBack) + 1 < controls.size())
            controls.add(controls.indexOf(controlInBack) + 1, control);
        else
            moveControlToBottom(control);
        refreshControls();
    }
    
    public default void moveControlAbove(CoreControl control, CoreControl controlInFront) {
        List controls = getControls();
        if (controls.contains(controlInFront) && controls.remove(control))
            controls.add(controls.indexOf(controlInFront), control);
        refreshControls();
    }
    
    public default void moveControlToTop(CoreControl control) {
        List controls = getControls();
        if (controls.remove(control))
            controls.add(0, control);
        refreshControls();
    }
    
    public default void moveControlToBottom(CoreControl control) {
        List controls = getControls();
        if (controls.remove(control))
            controls.add(control);
        refreshControls();
    }
}
