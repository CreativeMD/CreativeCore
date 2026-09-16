package team.creative.creativecore.common.config.premade;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;

import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.settings.KeyModifier;

public class KeyConfig {
    
    public static final KeyConfig UNBOUND = new KeyConfig(Type.KEYBOARD.ordinal(), -1, 3);
    
    public final int type;
    public final int key;
    public final int modifier;
    
    public KeyConfig(int type, int key, int modifier) {
        this.type = type;
        this.key = key;
        this.modifier = modifier;
    }
    
    public KeyConfig(int keyCode, KeyModifier modifier) {
        this(Type.KEYBOARD, keyCode, modifier);
    }
    
    public KeyConfig(InputConstants.Type type, int code, KeyModifier modifier) {
        this(type.ordinal(), code, modifier.ordinal());
    }
    
    public KeyConfig(InputConstants.Key key, KeyModifier modifier) {
        this(key.getType(), key.getValue(), modifier);
    }
    
    public KeyModifier getModifier() {
        return KeyModifier.values()[modifier];
    }
    
    public InputConstants.Key getKey() {
        if (isUnbound())
            return InputConstants.UNKNOWN;
        return InputConstants.Type.values()[type].getOrCreate(key);
    }
    
    public boolean isUnbound() {
        return key == -1;
    }
    
    public Component getTranslatedKeyMessage() {
        var key = getKey();
        return getModifier().getCombinedName(key, () -> key.getDisplayName());
    }
    
    public boolean matchesPress(InputEvent.Key key) {
        return matches(key.getKeyEvent()) && key.getAction() == InputConstants.PRESS && getModifier().isActive(null);
    }
    
    public boolean matchesPress(int key, int action) {
        return this.key == key && action == InputConstants.PRESS && getModifier().isActive(null);
    }
    
    public boolean matches(InputEvent.Key key) {
        return this.key == key.getKey() && getModifier().isActive(null);
    }
    
    public boolean matches(KeyEvent event) {
        return this.type == InputConstants.Type.KEYBOARD.ordinal() && this.key == event.key();
    }
    
    public boolean matchesMouse(MouseButtonEvent event) {
        return this.type == InputConstants.Type.MOUSE.ordinal() && this.key == event.button();
    }
    
    public boolean matches(int key) {
        return this.key == key && getModifier().isActive(null);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof KeyConfig key)
            return key.type == type && key.key == this.key && key.modifier == modifier;
        return false;
    }
    
}
