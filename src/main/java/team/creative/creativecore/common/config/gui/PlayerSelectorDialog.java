package team.creative.creativecore.common.config.gui;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.control.collection.GuiComboBox;
import team.creative.creativecore.common.gui.control.simple.GuiButton;
import team.creative.creativecore.common.util.player.PlayerSelector;
import team.creative.creativecore.common.util.text.TextMapBuilder;

public class PlayerSelectorDialog extends GuiLayer {
    
    public GuiPlayerSelectorButton button;
    public GuiPlayerSelectorHandler handler;
    
    public PlayerSelectorDialog(boolean client) {
        super(client, "playerselector", 150, 150);
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
        
        GuiComboBox<String> box = get("type");
        if (box != null)
            handler = GuiPlayerSelectorHandler.REGISTRY.get(box.selected());
        
        clear();
        
        box = new GuiComboBox<String>(this, "type", new TextMapBuilder<String>().addComponent(GuiPlayerSelectorHandler.REGISTRY.keys(), Component::literal));
        box.select(handler.getName());
        add(box);
        
        handler.createControls(this, selector);
        add(new GuiButton(this, "cancel", x -> closeTopLayer()).setTranslate("gui.cancel"));
        add(new GuiButton(this, "save", x -> {
            PlayerSelector parsed = handler.parseSelector(PlayerSelectorDialog.this);
            if (parsed != null) {
                PlayerSelectorDialog.this.button.set(parsed);
                closeTopLayer();
            }
        }).setTranslate("gui.save"));
    }
    
}
