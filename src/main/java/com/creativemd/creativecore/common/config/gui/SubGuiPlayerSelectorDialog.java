package com.creativemd.creativecore.common.config.gui;

import java.util.ArrayList;

import com.creativemd.creativecore.common.gui.container.SubGui;
import com.creativemd.creativecore.common.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiComboBox;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import com.creativemd.creativecore.common.utils.player.GuiPlayerSelectorHandler;
import com.creativemd.creativecore.common.utils.player.PlayerSelector;
import com.n247s.api.eventapi.eventsystem.CustomEventSubscribe;

import net.minecraft.nbt.NBTTagCompound;

public class SubGuiPlayerSelectorDialog extends SubGui {
    
    public final GuiPlayerSelectorButton button;
    public GuiPlayerSelectorHandler handler;
    
    public SubGuiPlayerSelectorDialog(GuiPlayerSelectorButton button) {
        super(150, 150);
        this.button = button;
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
                PlayerSelector selector = handler.parseSelector(SubGuiPlayerSelectorDialog.this);
                if (selector != null) {
                    SubGuiPlayerSelectorDialog.this.button.set(selector);
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
