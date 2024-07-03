package team.creative.creativecore.common.config.gui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.config.key.ConfigKeyField;
import team.creative.creativecore.common.config.key.ConfigKeyFieldType;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.flow.GuiFlow;

public class GuiConfigSubControlHolder extends GuiConfigSubControl {
    
    public ICreativeConfigHolder holder;
    public Object value;
    private final Runnable updateListener;
    
    public GuiConfigSubControlHolder(String name, ICreativeConfigHolder holder, Object value, Runnable updateListener) {
        super(name);
        setExpandable();
        this.holder = holder;
        this.value = value;
        this.updateListener = updateListener;
        flow = GuiFlow.STACK_Y;
    }
    
    public void load(ICreativeConfigHolder holder, Object value) {
        this.holder = holder;
        this.value = value;
    }
    
    public void createControls() {
        for (ConfigKeyField key : holder.fields()) {
            if (key.requiresRestart)
                continue;
            if (key.isFolder())
                continue;
            
            String path = "config." + String.join(".", holder.path());
            if (!path.endsWith("."))
                path += ".";
            String caption = translateOrDefault(path + key.name + ".name", key.name);
            String comment = path + key.name + ".comment";
            GuiConfigControl config = new GuiConfigControl((ConfigKeyFieldType) key, Side.SERVER, caption, comment) {
                
                @Override
                public void updateButton() {
                    super.updateButton();
                    updateListener.run();
                }
                
            };
            add(config);
            config.init(null);
            
        }
    }
    
    public void save() {
        JsonObject json = new JsonObject();
        for (GuiChildControl child : this.controls)
            if (child.control instanceof GuiConfigControl) {
                JsonElement element = ((GuiConfigControl) child.control).save();
                if (element != null)
                    json.add(((GuiConfigControl) child.control).field.name, element);
            }
        
        holder.load(provider(), false, true, json, Side.SERVER);
    }
    
}
