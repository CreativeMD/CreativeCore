package team.creative.creativecore.common.config.gui;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.control.collection.GuiComboBox;
import team.creative.creativecore.common.gui.control.parent.GuiLeftRightBox;
import team.creative.creativecore.common.gui.control.simple.GuiButton;
import team.creative.creativecore.common.gui.flow.GuiFlow;
import team.creative.creativecore.common.util.player.PlayerSelector;
import team.creative.creativecore.common.util.text.TextMapBuilder;

public class PlayerSelectorDialog extends GuiLayer {
    
    public GuiParent settings;
    public GuiPlayerSelectorButton button;
    public GuiPlayerSelectorHandler handler;
    
    public PlayerSelectorDialog() {
        super("playerselector", 150, 150);
        registerEventChanged(event -> {
            if (event.control.is("type")) {
                GuiComboBox<GuiPlayerSelectorHandler> box = (GuiComboBox<GuiPlayerSelectorHandler>) event.control;
                settings.clear();
                handler = box.selected();
                handler.createControls(settings, button.get());
                reflow();
            } else
                handler.onChanged(this, event);
        });
        flow = GuiFlow.STACK_Y;
    }
    
    @Override
    public void create() {
        if (button == null)
            return;
        
        PlayerSelector selector = button.get();
        handler = GuiPlayerSelectorHandler.get(selector);
        
        var box = new GuiComboBox<GuiPlayerSelectorHandler>("type", new TextMapBuilder<GuiPlayerSelectorHandler>().addComponent(GuiPlayerSelectorHandler.REGISTRY.values(),
            x -> Component.literal(x.getName())));
        add(box.setExpandableX());
        
        add(settings = new GuiParent(GuiFlow.STACK_Y));
        settings.setExpandable();
        
        GuiLeftRightBox bottom = new GuiLeftRightBox();
        add(bottom);
        bottom.addLeft(new GuiButton("Cancel", x -> closeTopLayer()).setTranslate("gui.cancel"));
        bottom.addRight(new GuiButton("Save", x -> {
            PlayerSelector parsed = handler.parseSelector(PlayerSelectorDialog.this);
            if (parsed != null) {
                PlayerSelectorDialog.this.button.set(parsed);
                closeTopLayer();
            }
        }).setTranslate("gui.save"));
        
        box.select(handler);
    }
    
}
