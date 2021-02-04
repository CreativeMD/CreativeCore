package team.creative.creativecore.common.config.gui;

import com.google.gson.JsonElement;

import net.minecraftforge.api.distmarker.Dist;
import team.creative.creativecore.common.config.holder.ConfigKey.ConfigKeyField;
import team.creative.creativecore.common.gui.GuiParent;
import team.creative.creativecore.common.gui.controls.GuiButton;
import team.creative.creativecore.common.gui.style.ControlFormatting;

public class GuiConfigControl extends GuiParent {
	
	public final ConfigKeyField field;
	public final Dist side;
	private GuiButton resetButton;
	
	public GuiConfigControl(ConfigKeyField field, int x, int y, int width, int height, Dist side) {
		super(field.name, x, y, width, height);
		this.field = field;
		this.side = side;
	}
	
	@Override
	public ControlFormatting getControlFormatting() {
		return ControlFormatting.TRANSPARENT;
	}
	
	public void setResetButton(GuiButton resetButton) {
		this.resetButton = resetButton;
		updateButton();
	}
	
	public void updateButton() {
		this.resetButton.enabled = !field.isDefault(field.converation.save(this, field.getType(), field), side);
	}
	
	public void init(JsonElement initalValue) {
		field.converation.createControls(this, field, field.getType(), width - 20);
		field.converation.loadValue(initalValue != null ? field.converation.readElement(field.getDefault(), false, false, initalValue, side, field) : field.get(), this, field);
	}
	
	public void reset() {
		field.converation.loadValue(field.getDefault(), this, field);
		updateButton();
	}
	
	public void changed() {
		updateButton();
	}
	
	public JsonElement save() {
		Object value = field.converation.save(this, field.getType(), field);
		if (!field.get().equals(value))
			return field.converation.writeElement(value, field.getDefault(), true, false, side, field);
		return null;
	}
	
}
