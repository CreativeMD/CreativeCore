package com.creativemd.creativecore.common.config.gui;

import com.creativemd.creativecore.common.config.holder.ConfigKey;
import com.creativemd.creativecore.common.config.holder.ConfigKey.ConfigKeyField;
import com.creativemd.creativecore.common.config.holder.ICreativeConfigHolder;
import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiLabel;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraftforge.fml.relauncher.Side;

public class GuiConfigSubControlHolder extends GuiConfigSubControl {
    
    public final ICreativeConfigHolder holder;
    public final Object value;
    
    public GuiConfigSubControlHolder(String name, int x, int y, int width, int height, ICreativeConfigHolder holder, Object value) {
        super(name, x, y, width, height);
        this.holder = holder;
        this.value = value;
    }
    
    public void createControls() {
        int offsetX = 1;
        int offsetY = 1;
        for (ConfigKey key : holder.fields()) {
            if (key.requiresRestart)
                continue;
            Object value = key.get();
            String caption = translateOrDefault("config." + String.join(".", holder.path()) + "." + key.name + ".name", key.name);
            String comment = "config." + String.join(".", holder.path()) + "." + key.name + ".comment";
            if (value instanceof ICreativeConfigHolder)
                continue;
            
            GuiLabel label = new GuiLabel(caption + ":", offsetX, offsetY + 2);
            
            GuiConfigControl config = new GuiConfigControl((ConfigKeyField) key, 0, offsetY, 100, 14, Side.SERVER);
            GuiButton resetButton = new GuiButton("r", offsetX + 390, offsetY, 14) {
                
                @Override
                public void onClicked(int x, int y, int button) {
                    config.reset();
                }
            };
            
            int labelWidth = 40;
            config.posX = label.posX + labelWidth + 2;
            config.width = width - 25 - config.posX;
            config.init(null);
            addControl(label.setLangTooltip(comment));
            addControl(config);
            addControl(resetButton.setCustomTooltip("reset to default"));
            config.setResetButton(resetButton);
            offsetY += config.height + 1;
            
        }
        
        height = offsetY;
    }
    
    public void save() {
        JsonObject json = new JsonObject();
        for (GuiControl control : controls)
            if (control instanceof GuiConfigControl) {
                JsonElement element = ((GuiConfigControl) control).save();
                if (element != null)
                    json.add(((GuiConfigControl) control).field.name, element);
            }
        
        holder.load(false, true, json, Side.SERVER);
    }
    
}
