package team.creative.creativecore.common.config.gui;

import com.google.gson.JsonElement;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.holder.ConfigKey.ConfigKeyField;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.controls.parent.GuiColumn;
import team.creative.creativecore.common.gui.controls.parent.GuiRow;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.controls.simple.GuiLabel;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.text.TextBuilder;

public class GuiConfigControl extends GuiRow {
    
    public final ConfigKeyField field;
    public final Side side;
    private GuiButton resetButton;
    private final GuiColumn main;
    
    public GuiConfigControl(ConfigGuiLayer layer, ConfigKeyField field, Side side, String caption, String comment) {
        super();
        this.field = field;
        this.side = side;
        this.setExpandableX();
        GuiColumn text = new GuiColumn(100);
        text.valign = VAlign.CENTER;
        addColumn(text);
        text.add(new GuiLabel(caption + ":").setTitle(Component.literal(caption + ":")).setTooltip(new TextBuilder().translateIfCan(comment).build()));
        addColumn(main = (GuiColumn) new GuiColumn(200).setExpandableX());
        GuiColumn end = new GuiColumn(20);
        end.align = Align.CENTER;
        addColumn(end);
        
        this.resetButton = (GuiButton) new GuiButton("r", x -> {
            GuiConfigControl.this.reset();
            if (layer != null)
                layer.changed = true;
        }).setTitle(Component.literal("r")).setAlign(Align.CENTER);
        end.add(resetButton.setTooltip(new TextBuilder().text("reset to default").build()));
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT;
    }
    
    public void updateButton() {
        this.resetButton.enabled = !field.isDefault(field.converation.save(main, field.getType(), field), side);
    }
    
    public void init(JsonElement initalValue) {
        field.converation.createControls(main, field, field.getType());
        field.converation.loadValue(initalValue != null ? field.converation.readElement(field.getDefault(), false, false, initalValue, side, field) : field.get(), main, field);
        
        updateButton();
    }
    
    public void reset() {
        field.converation.loadValue(field.getDefault(), main, field);
        updateButton();
    }
    
    public void changed() {
        updateButton();
    }
    
    public JsonElement save() {
        Object value = field.converation.save(main, field.getType(), field);
        if (!field.get().equals(value))
            return field.converation.writeElement(value, field.getDefault(), true, false, side, field);
        return null;
    }
    
}
