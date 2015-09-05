package com.creativemd.creativecore.common.gui.controls;

import java.util.ArrayList;

import javax.vecmath.Vector2d;

import com.creativemd.creativecore.common.gui.event.ControlChangedEvent;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;

public class GuiComboBoxExtension extends GuiListBox{
	
	public GuiComboBox comboBox;
	
	public GuiComboBoxExtension(String name, EntityPlayer player, GuiComboBox comboBox, int x, int y, int width, int height, ArrayList<String> lines) {
		super(name, player, x, y, width, height, lines);
		this.comboBox = comboBox;
		this.selected = lines.indexOf(comboBox.caption);
	}
	
	@Override
	public Vector2d getCenterOffset()
	{
		return new Vector2d(width/2, -comboBox.height/2);
	}
	
	public void onLoseFocus()
	{
		if(!comboBox.isMouseOver())
			comboBox.closeBox();
	}
	
	public void onSelectionChange()
	{
		if(selected != -1 && selected < lines.size())
		{
			comboBox.caption = lines.get(selected);
			comboBox.raiseEvent(new ControlChangedEvent(comboBox));
		}
		comboBox.closeBox();
	}

}
