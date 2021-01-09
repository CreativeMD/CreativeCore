package com.creativemd.creativecore.client.key;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.IKeyConflictContext;

public class ExtendedKeyBinding extends KeyBinding {
    
    public ExtendedKeyBinding(String description, int keyCode, String category) {
        super(description, keyCode, category);
    }
    
    public ExtendedKeyBinding(String description, IKeyConflictContext keyConflictContext, int keyCode, String category) {
        super(description, keyConflictContext, keyCode, category);
    }
    
    public ExtendedKeyBinding(String description, net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext, net.minecraftforge.client.settings.KeyModifier keyModifier, int keyCode, String category) {
        super(description, keyConflictContext, keyModifier, keyCode, category);
    }
    
    public static final int timeToWaitFirst = 500;
    public static final int timeToWait = 20;
    
    protected long lastTimePressed = -1;
    protected boolean pressedBefore = false;
    protected boolean pressedFirst = false;
    
    public boolean isPressed(boolean repeated) {
        if (repeated)
            return isPressed();
        return super.isPressed();
    }
    
    @Override
    public boolean isPressed() {
        boolean isKeyDown = isKeyDown();
        if (isKeyDown) {
            if (!pressedBefore) {
                lastTimePressed = System.currentTimeMillis();
                pressedFirst = false;
            } else {
                long elapsed = System.currentTimeMillis() - lastTimePressed;
                if (!pressedFirst) {
                    if (elapsed >= timeToWaitFirst) {
                        pressedFirst = true;
                        lastTimePressed = System.currentTimeMillis();
                        return true;
                    }
                } else if (elapsed >= timeToWait) {
                    lastTimePressed = System.currentTimeMillis();
                    return true;
                }
            }
        } else
            pressedFirst = false;
        pressedBefore = isKeyDown;
        return super.isPressed();
    }
}
