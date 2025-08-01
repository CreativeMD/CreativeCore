package team.creative.creativecore.common.config.gui;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.config.key.ConfigKey;
import team.creative.creativecore.common.config.key.ConfigKeyType;
import team.creative.creativecore.common.gui.GuiControl;
import team.creative.creativecore.common.gui.IGuiParent;
import team.creative.creativecore.common.gui.flow.GuiFlow;

public class GuiConfigSubControlHolder extends GuiConfigSubControl {
    
    public ICreativeConfigHolder holder;
    public Object value;
    private final Runnable updateListener;
    private final Side side;
    
    public GuiConfigSubControlHolder(IGuiParent parent, String name, ICreativeConfigHolder holder, Object value, Side side, @Nullable Runnable updateListener) {
        super(parent, name);
        setExpandable();
        this.holder = holder;
        this.value = value;
        this.side = side;
        this.updateListener = updateListener;
        setFlow(GuiFlow.STACK_Y);
    }
    
    public void load(ICreativeConfigHolder holder, Object value) {
        this.holder = holder;
        this.value = value;
    }
    
    public void createControls() {
        for (ConfigKey key : holder.fields()) {
            if (key.requiresRestart || key.isFolder() || key.hideFromGUI)
                continue;
            
            String path = "config." + String.join(".", holder.path());
            if (!path.endsWith("."))
                path += ".";
            String caption = translateOrDefault(path + key.name + ".name", key.name);
            String comment = path + key.name + ".comment";
            GuiConfigControl config = new GuiConfigControl(this, (ConfigKeyType) key, side, caption, comment) {
                
                @Override
                public void updateButton() {
                    super.updateButton();
                    if (updateListener != null)
                        updateListener.run();
                }
                
            };
            add(config);
            config.init(null);
            
        }
    }
    
    public void save() {
        JsonObject json = new JsonObject();
        for (GuiControl control : this)
            if (control instanceof GuiConfigControl c) {
                JsonElement element = c.save();
                if (element != null)
                    json.add(c.field.name, element);
            }
        
        holder.load(provider(), false, true, json, side);
    }
    
}
