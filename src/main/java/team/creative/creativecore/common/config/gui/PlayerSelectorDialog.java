package team.creative.creativecore.common.config.gui;

import java.util.ArrayList;

import com.n247s.api.eventapi.eventsystem.CustomEventSubscribe;

import net.minecraft.nbt.NBTTagCompound;
import team.creative.creativecore.common.gui.GuiLayer;
import team.creative.creativecore.common.gui.controls.collection.GuiComboBox;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.event.GuiControlChangedEvent;
import team.creative.creativecore.common.util.player.PlayerSelector;

public class PlayerSelectorDialog extends GuiLayer {
    
    public GuiPlayerSelectorButton button;
    public GuiPlayerSelectorHandler handler;
    
    public PlayerSelectorDialog() {
        super("playerselector", 150, 150);
    }
    
    @Override
    public void createControls() {
        PlayerSelector selector = button.get();
        handler = GuiPlayerSelectorHandler.getHandler(selector);
        
        GuiComboBox box = (GuiComboBox) get("type");
        if (box != null)
            handler = GuiPlayerSelectorHandler.getHandler(box.getCaption());
        
        controls.clear();
        ArrayList<String> lines = new ArrayList<String>(GuiPlayerSelectorHandler.getNames());
        
        box = new GuiComboBox("type", 0, 0, 144, lines);
        box.select(lines.indexOf(handler.getName()));
        controls.add(box);
        
        handler.createControls(this, selector);
        
        controls.add(new GuiButton("Cancel", 0, 130, 41) {
            
            @Override
            public void onClicked(int x, int y, int button) {
                closeLayer(new NBTTagCompound());
            }
        });
        controls.add(new GuiButton("Save", 103, 130, 41) {
            
            @Override
            public void onClicked(int x, int y, int button) {
                PlayerSelector selector = handler.parseSelector(PlayerSelectorDialog.this);
                if (selector != null) {
                    PlayerSelectorDialog.this.button.set(selector);
                    closeLayer(new NBTTagCompound());
                }
            }
        });
    }
    
    @CustomEventSubscribe
    public void onChanged(GuiControlChangedEvent event) {
        if (event.source.is("type")) {
            createControls();
            refreshControls();
        } else
            handler.onChanged(this, event);
    }
    
}
