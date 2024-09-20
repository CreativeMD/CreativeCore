package team.creative.creativecore.common.config.gui;

import com.google.gson.JsonElement;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.Side;
import team.creative.creativecore.common.config.key.ConfigKeyType;
import team.creative.creativecore.common.gui.Align;
import team.creative.creativecore.common.gui.VAlign;
import team.creative.creativecore.common.gui.controls.parent.GuiColumn;
import team.creative.creativecore.common.gui.controls.parent.GuiRow;
import team.creative.creativecore.common.gui.controls.simple.GuiButton;
import team.creative.creativecore.common.gui.controls.simple.GuiLabel;
import team.creative.creativecore.common.gui.flow.GuiSizeRule.GuiSizeRatioRules;
import team.creative.creativecore.common.gui.style.ControlFormatting;
import team.creative.creativecore.common.util.text.TextBuilder;

public class GuiConfigControl extends GuiRow implements IGuiConfigParent {
    
    public final ConfigKeyType field;
    public final Side side;
    private GuiButton resetButton;
    private final GuiColumn main;
    private Object extra;
    private boolean loading = false;
    
    public GuiConfigControl(ConfigKeyType field, Side side, int width, boolean showReset) {
        this(field, side, null, null, width, showReset);
    }
    
    public GuiConfigControl(ConfigKeyType field, Side side, String caption, String comment) {
        this(field, side, caption, comment, 200, true);
    }
    
    public GuiConfigControl(ConfigKeyType field, Side side, String caption, String comment, int width, boolean showReset) {
        super();
        this.field = field;
        this.side = side;
        if (caption != null) {
            this.setExpandableX();
            GuiColumn text = (GuiColumn) new GuiColumn().setDim(new GuiSizeRatioRules().maxWidth(200));
            text.valign = VAlign.CENTER;
            addColumn(text);
            text.add(new GuiLabel(caption + ":").setTitle(Component.literal(caption + ":")).setTooltip(new TextBuilder().translateIfCan(comment).build()));
        }
        
        addColumn(main = (GuiColumn) new GuiColumn(width).setExpandableX());
        
        if (showReset) {
            GuiColumn end = new GuiColumn(20);
            end.align = Align.CENTER;
            addColumn(end);
            this.resetButton = (GuiButton) new GuiButton("r", x -> GuiConfigControl.this.reset()).setTranslate("gui.config.reset").setAlign(Align.CENTER);
            end.add(resetButton.setTooltip(new TextBuilder().text("reset to default").build()));
        }
        
        registerEventChanged(x -> {
            if (!loading)
                changed();
        });
    }
    
    @Override
    public ControlFormatting getControlFormatting() {
        return ControlFormatting.TRANSPARENT;
    }
    
    public boolean isDefault() {
        return !field.isDefault(field.converation.save(main, this, field, side), side);
    }
    
    public void updateButton() {
        if (resetButton != null)
            this.resetButton.enabled = isDefault();
    }
    
    public void init(JsonElement initalValue) {
        loading = true;
        field.converation.createControls(main, this, field, side);
        field.converation.loadValue(initalValue != null ? field.converation.readElement(provider(), field.defaultValue, false, false, initalValue, side, field) : field.get(),
            field.defaultValue, main, this, field, side);
        loading = false;
        
        updateButton();
    }
    
    public void reset() {
        field.converation.restoreDefault(field.defaultValue, main, this, field, side);
        updateButton();
    }
    
    @Override
    public void changed() {
        updateButton();
    }
    
    public JsonElement save() {
        Object value = field.converation.save(main, this, field, side);
        if (field.converation.shouldSave(value, main, this, field, side))
            return field.converation.writeElement(provider(), value, true, false, side, field);
        return null;
    }
    
    @Override
    public void setCustomData(Object object) {
        this.extra = object;
    }
    
    @Override
    public Object getCustomData() {
        return extra;
    }
    
}
