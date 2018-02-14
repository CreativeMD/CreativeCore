package com.creativemd.creativecore.gui.controls.gui;

import java.util.ArrayList;

import javax.vecmath.Vector2d;

import com.creativemd.creativecore.gui.event.gui.GuiControlChangedEvent;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

public class GuiComboBoxExtension extends GuiListBox {
	
	public GuiComboBox comboBox;
	
	public GuiComboBoxExtension(String name, GuiComboBox comboBox, int x, int y, int width, int height, ArrayList<String> lines) {
		super(name, x, y, width, height, lines);
		this.comboBox = comboBox;
		this.selected = lines.indexOf(comboBox.caption);
		reloadControls();
	}
	
	@Override
	public Vec3d getCenterOffset()
	{
		return new Vec3d(width/2, -comboBox.height/2, 0);
	}
	
	public void onLoseFocus()
	{
		if(!comboBox.isMouseOver() && !isMouseOver())
			comboBox.closeBox();
	}
	
	@Override
	public void onSelectionChange()
	{
		if(selected != -1 && selected < lines.size())
		{
			comboBox.caption = lines.get(selected);
			comboBox.index = selected;
			comboBox.raiseEvent(new GuiControlChangedEvent(comboBox));
		}
		comboBox.closeBox();
	}

}
