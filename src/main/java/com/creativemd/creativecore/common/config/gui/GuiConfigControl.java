package com.creativemd.creativecore.common.config.gui;

import com.creativemd.creativecore.common.config.ConfigTypeConveration;
import com.creativemd.creativecore.common.config.holder.ConfigHolderObject.ConfigKeyField;
import com.creativemd.creativecore.common.gui.container.GuiParent;
import com.creativemd.creativecore.common.gui.controls.gui.GuiButton;
import com.google.gson.JsonElement;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiConfigControl extends GuiParent {
	
	public final ConfigTypeConveration converation;
	public final ConfigKeyField field;
	public final Side side;
	private GuiButton resetButton;
	
	protected boolean changed = false;
	
	public GuiConfigControl(ConfigKeyField field, int x, int y, int width, int height, Side side) {
		super(field.name, x, y, width, height);
		this.field = field;
		this.borderWidth = this.marginWidth = 0;
		this.converation = ConfigTypeConveration.get(field.getType());
		this.side = side;
	}
	
	public void setResetButton(GuiButton resetButton) {
		this.resetButton = resetButton;
		updateButton();
	}
	
	public void updateButton() {
		this.resetButton.enabled = !field.isDefault(side) || (changed && !field.isDefault(converation.save(this, field.getType(), field), side));
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
		converation.createControls(this, field, field.getType(), 100);
		converation.loadValue(initalValue != null ? converation.readElement(field.getDefault(), false, initalValue, side) : field.get(), this);
	}
	
	public void reset() {
		converation.loadValue(field.getDefault(), this);
		changed = false;
		updateButton();
	}
	
	public void changed() {
		changed = true;
		updateButton();
	}
	
	public JsonElement save() {
		Object value = converation.save(this, field.getType(), field);
		if (!field.get().equals(value))
			return converation.writeElement(value, field.getDefault(), true, side);
		return null;
	}
	
}
