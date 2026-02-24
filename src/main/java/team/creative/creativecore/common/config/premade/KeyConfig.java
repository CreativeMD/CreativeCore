package team.creative.creativecore.common.config.premade;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;

import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.settings.KeyModifier;

public class KeyConfig {
    
    public static final KeyConfig UNBOUND = new KeyConfig(-1, -1, 3);
    
    public final int keyCode;
    public final int scanCode;
    public final int modifier;
    
    public KeyConfig(int keyCode, int scanCode, int modifier) {
        this.keyCode = keyCode;
        this.scanCode = scanCode;
        this.modifier = modifier;
    }
    
    @OnlyIn(Dist.CLIENT)
    public KeyConfig(int keyCode, KeyModifier modifier) {
        this(Type.KEYSYM, keyCode, modifier);
    }
    
    @OnlyIn(Dist.CLIENT)
    public KeyConfig(InputConstants.Type type, int code, KeyModifier modifier) {
        this(type == Type.KEYSYM ? code : -1, type == Type.SCANCODE ? code : -1, modifier.ordinal());
    }
    
    @OnlyIn(Dist.CLIENT)
    public KeyConfig(InputConstants.Key key, KeyModifier modifier) {
        this(key.getType(), key.getValue(), modifier);
    }
    
    @OnlyIn(Dist.CLIENT)
    public KeyModifier getModifier() {
        return KeyModifier.values()[modifier];
    }
    
    @OnlyIn(Dist.CLIENT)
    public InputConstants.Key getKey() {
        if (isUnbound())
            return InputConstants.UNKNOWN;
        return InputConstants.getKey(new KeyEvent(keyCode, scanCode, modifier));
    }
    
    public boolean isUnbound() {
        return keyCode == -1 && scanCode == -1;
    }
    
    public Component getTranslatedKeyMessage() {
        var key = getKey();
        return getModifier().getCombinedName(key, () -> key.getDisplayName());
    }
    
    public boolean matches(InputEvent.Key key) {
        return this.keyCode == key.getKey() && key.getAction() == InputConstants.PRESS && getModifier().isActive(null);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof KeyConfig key)
            return key.keyCode == keyCode && key.scanCode == scanCode && key.modifier == modifier;
        return false;
    }
    
}
