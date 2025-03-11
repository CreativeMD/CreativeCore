package team.creative.creativecore.common.config.gui;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.holder.ICreativeConfigHolder;
import team.creative.creativecore.common.config.key.ConfigKey;
import team.creative.creativecore.common.config.key.ConfigKeyType;
import team.creative.creativecore.common.gui.GuiChildControl;
import team.creative.creativecore.common.gui.control.parent.GuiPanel;
import team.creative.creativecore.common.gui.control.simple.GuiLabel;
import team.creative.creativecore.common.gui.control.simple.GuiTextfield;
import team.creative.creativecore.common.gui.flow.GuiFlow;

public class GuiConfigSubControlNested extends GuiConfigSubControl {
    
    public ICreativeConfigHolder holder;
    public Object value;
    private final Runnable updateListener;
    private final Side side;
    private final GuiPanel panel;
    
    public GuiConfigSubControlNested(String name, ICreativeConfigHolder holder, Object value, Side side, @Nullable Runnable updateListener) {
        super(name);
        setExpandable();
        this.panel = new GuiPanel(GuiFlow.STACK_Y);
        add(panel);
        this.holder = holder;
        this.value = value;
        this.side = side;
        this.updateListener = updateListener;
        flow = GuiFlow.STACK_Y;
    }
    
    public void load(ICreativeConfigHolder holder, Object value) {
        this.holder = holder;
        this.value = value;
    }
    
    @Override
    public void addNameUnmodifieable(String name) {
        nameLabel = new GuiLabel("title").setTitle(Component.literal(name));
        panel.add(nameLabel);
    }
    
    @Override
    public void addNameTextfield(String name) {
        panel.add(nameField = new GuiTextfield("title", name).setDim(50, 8));
    }
    
    public void createControls() {
        for (ConfigKey key : holder.fields()) {
            if (key.hideFromGUI)
                continue;
            String path = "config." + String.join(".", holder.path());
            if (!path.endsWith("."))
                path += ".";
            String caption = translateOrDefault(path + key.name + ".name", key.name);
            String comment = path + key.name + ".comment";
            
            if (key.isFolder()) {
                GuiConfigSubControlNested config = new GuiConfigSubControlNested(key.name, key.holder(), key.field().get(), side, updateListener);
                panel.add(config);
                config.addNameUnmodifieable(caption);
                config.createControls();
            } else {
                GuiConfigControl config = new GuiConfigControl((ConfigKeyType) key, side, caption, comment) {
                    
                    @Override
                    public void updateButton() {
                        super.updateButton();
                        if (updateListener != null)
                            updateListener.run();
                    }
                    
                };
                panel.add(config);
                config.init(null);
            }
            
        }
    }
    
    public JsonObject save() {
        JsonObject json = new JsonObject();
        for (GuiChildControl child : this.controls)
            if (child.control instanceof GuiConfigControl c) {
                JsonElement element = c.save();
                if (element != null)
                    json.add(c.field.name, element);
            } else if (child.control instanceof GuiConfigSubControlNested n)
                json.add(n.name, n.save());
            
        holder.load(provider(), false, true, json, side);
        return holder.save(provider(), false, true, side);
    }
    
}
