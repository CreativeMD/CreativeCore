package com.creativemd.creativecore.common.config.gui;

import com.creativemd.creativecore.common.config.holder.ConfigKey.ConfigKeyField;
import com.creativemd.creativecore.common.gui.container.GuiParent;
import com.creativemd.creativecore.common.gui.controls.gui.GuiButton;
import com.google.gson.JsonElement;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiConfigControl extends GuiParent {
	
	public final ConfigKeyField field;
	public final Side side;
	private GuiButton resetButton;
	
	public GuiConfigControl(ConfigKeyField field, int x, int y, int width, int height, Side side) {
		super(field.name, x, y, width, height);
		this.field = field;
		this.borderWidth = this.marginWidth = 0;
		this.side = side;
	}
	
	public void setResetButton(GuiButton resetButton) {
		this.resetButton = resetButton;
		updateButton();
	}
	
	public void updateButton() {
		this.resetButton.enabled = !field.isDefault(field.converation.save(this, field.getType(), field), side);
	}
	
	@Override
	public boolean hasBorder() {
		return false;
	}
	
	@Override
	public boolean hasBackground() {
		return false;
	}
	
	@Override
	public boolean hasMouseOverEffect() {
		return false;
	}
	
	public void init(JsonElement initalValue) {
		field.converation.createControls(this, field, field.getType(), 100);
		field.converation.loadValue(initalValue != null ? field.converation.readElement(field.getDefault(), false, initalValue, side, field) : field.get(), this, field);
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
			return field.converation.writeElement(value, field.getDefault(), true, side, field);
		return null;
	}
	
}
