package team.creative.creativecore.common.config.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.controls.collection.GuiComboBoxMapped;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.util.player.PlayerSelector;
import team.creative.creativecore.common.util.text.TextMapBuilder;

public class PlayerSelectorDialog extends GuiLayer {
    
    public GuiPlayerSelectorButton button;
    public GuiPlayerSelectorHandler handler;
    
    public PlayerSelectorDialog() {
        super("playerselector", 150, 150);
        registerEventChanged(event -> {
            if (event.control.is("type")) {
                reinit();
            } else
                handler.onChanged(this, event);
        });
    }
    
    @Override
    public void create() {
        PlayerSelector selector = button.get();
        handler = GuiPlayerSelectorHandler.get(selector);
        
        GuiComboBoxMapped<String> box = get("type");
        if (box != null)
            handler = GuiPlayerSelectorHandler.REGISTRY.get(box.getSelected());
        
        clear();
        
        box = new GuiComboBoxMapped<String>("type", new TextMapBuilder<String>().addComponent(GuiPlayerSelectorHandler.REGISTRY.keys(), TextComponent::new));
        box.select(handler.getName());
        add(box);
        
        handler.createControls(this, selector);
        add(new GuiButton("Cancel", x -> closeTopLayer()));
        add(new GuiButton("Save", x -> {
            PlayerSelector parsed = handler.parseSelector(PlayerSelectorDialog.this);
            if (parsed != null) {
                PlayerSelectorDialog.this.button.set(parsed);
                closeTopLayer();
            }
        }));
    }
    
}
