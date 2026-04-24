package team.creative.creativecore.client.config.gui;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.ChatFormatting;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.settings.KeyModifier;
import team.creative.creativecore.client.gui.control.simple.GuiClientButton;
import team.creative.creativecore.common.config.gui.GuiButtonKeyConfig;
import team.creative.creativecore.common.config.gui.GuiButtonKeyConfig.GuiButtonKeyConfigDist;
import team.creative.creativecore.common.config.premade.KeyConfig;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;

public class GuiClientButtonKeyConfig extends GuiClientButton<GuiButtonKeyConfig> implements GuiButtonKeyConfigDist {
    
    protected KeyConfig key;
    private boolean waiting;
    
    private InputConstants.Key lastPressedKey = InputConstants.UNKNOWN;
    private InputConstants.Key lastPressedModifier = InputConstants.UNKNOWN;
    private boolean isLastKeyHeldDown = false;
    private boolean isLastModifierHeldDown = false;
    
    public GuiClientButtonKeyConfig(GuiButtonKeyConfig control) {
        super(control);
        pressed = x -> {
            if (!waiting) {
                waiting = true;
                updateTitle();
            }
        };
    }
    
    @Override
    public void setValue(KeyConfig key) {
        this.key = key;
        updateTitle();
    }
    
    @Override
    public KeyConfig getValue() {
        return key;
    }
    
    protected void setKeyPressed(KeyConfig config) {
        this.waiting = false;
        this.key = config;
        updateTitle();
        if (getParent() != null)
            raiseEvent(new GuiControlChangedEvent<>(control));
    }
    
    @Override
    public boolean keyReleased(KeyEvent event) {
        // We ignore events from keys with the scan code 63 as they're emitted
        // (only as RELEASE, not PRESS) by Mac systems to indicate that "Fn" is being pressed
        // See https://github.com/neoforged/NeoForge/issues/1683
        if (waiting && (!net.minecraft.client.input.InputQuirks.ON_OSX || event.scancode() != 63)) {
            if (event.key() == InputConstants.KEY_ESCAPE) {
                setKeyPressed(KeyConfig.UNBOUND);
                lastPressedKey = InputConstants.UNKNOWN;
                lastPressedModifier = InputConstants.UNKNOWN;
                isLastKeyHeldDown = false;
                isLastModifierHeldDown = false;
                return true;
            }
            
            var key = InputConstants.getKey(event);
            if (lastPressedKey.equals(key))
                isLastKeyHeldDown = false;
            else if (lastPressedModifier.equals(key))
                isLastModifierHeldDown = false;
            
            if (!isLastKeyHeldDown && !isLastModifierHeldDown) {
                if (!lastPressedKey.equals(InputConstants.UNKNOWN))
                    setKeyPressed(new KeyConfig(lastPressedKey, KeyModifier.getKeyModifier(lastPressedModifier)));
                else
                    setKeyPressed(new KeyConfig(lastPressedModifier, KeyModifier.NONE));
                lastPressedKey = InputConstants.UNKNOWN;
                lastPressedModifier = InputConstants.UNKNOWN;
            }
            return true;
        }
        return super.keyReleased(event);
    }
    
    @Override
    public boolean keyPressed(KeyEvent event) {
        if (waiting) {
            var key = InputConstants.getKey(event);
            if (lastPressedModifier == InputConstants.UNKNOWN && net.neoforged.neoforge.client.settings.KeyModifier.isKeyCodeModifier(key)) {
                lastPressedModifier = key;
                isLastModifierHeldDown = true;
            } else {
                lastPressedKey = key;
                isLastKeyHeldDown = true;
            }
            return true;
        }
        return super.keyPressed(event);
    }
    
    public void updateTitle() {
        if (waiting)
            setTitle(Component.literal("> ").append(key.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE)).append(" <").withStyle(
                ChatFormatting.YELLOW));
        else
            setTitle(key.getTranslatedKeyMessage());
    }
    
    @Override
    public void looseFocus() {
        if (waiting) {
            waiting = false;
            updateTitle();
        }
        super.looseFocus();
    }
    
}
