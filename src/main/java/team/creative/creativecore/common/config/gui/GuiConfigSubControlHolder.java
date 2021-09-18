package team.creative.creativecore.common.config.gui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraftforge.api.distmarker.Dist;
import team.creative.creativecore.common.config.holder.ConfigKey;
import team.creative.creativecore.common.config.holder.ConfigKey.ConfigKeyField;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.gui.GuiChildControl;

public class GuiConfigSubControlHolder extends GuiConfigSubControl {
    
    public final ICreativeConfigHolder holder;
    public final Object value;
    
    public GuiConfigSubControlHolder(String name, ICreativeConfigHolder holder, Object value) {
        super(name);
        setExpandable();
        this.holder = holder;
        this.value = value;
    }
    
    public void createControls() {
        for (ConfigKey key : holder.fields()) {
            if (key.requiresRestart)
                continue;
            Object value = key.get();
            
            if (value instanceof ICreativeConfigHolder)
                continue;
            
            String caption = translateOrDefault("config." + String.join(".", holder.path()) + "." + key.name + ".name", key.name);
            String comment = "config." + String.join(".", holder.path()) + "." + key.name + ".comment";
            GuiConfigControl config = new GuiConfigControl(null, (ConfigKeyField) key, Dist.DEDICATED_SERVER, caption, comment);
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
        
        holder.load(false, true, json, Dist.DEDICATED_SERVER);
    }
    
}
