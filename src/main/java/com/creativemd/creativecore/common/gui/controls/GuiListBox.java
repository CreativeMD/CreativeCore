package com.creativemd.creativecore.common.gui.controls;

import java.util.ArrayList;

import com.creativemd.creativecore.common.gui.event.ControlClickEvent;
import com.n247s.api.eventapi.eventsystem.CustomEventSubscribe;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;

public class GuiListBox extends GuiScrollBox{
	
	protected ArrayList<String> lines = new ArrayList<String>();
	
	public int selected = -1;
	
	public GuiListBox(String name, EntityPlayer player, int x, int y, int width, int height, ArrayList<String> lines) {
		super(name, player, x, y, width, height);
		this.lines = lines;
		refreshControls();
		
	}
	
	@Override
	public void init()
	{
		parent.addListener(this);
	}
	
	public int size()
	{
		return lines.size();
	}
	
	public String get(int id)
	{
		return lines.get(id);
	}
	
	public void add(String input)
	{
		lines.add(input);
		refreshControls();
	}
	
	public void remove(int id)
	{
		lines.remove(id);
		refreshControls();
	}
	
	public void refreshControls()
	{
		gui.controls.clear();
		for (int i = 0; i < lines.size(); i++) {
			int color = 14737632;
			if(i == selected)
				color = 16777000;
			GuiLabelClickable label = new GuiLabelClickable(lines.get(i), 3, 1+i*15, color);
			label.width = width-20;
			label.height = 15;
			addControl(label);
		}
	}
	
	@CustomEventSubscribe
	public void onLabelClicked(ControlClickEvent event)
	{
		if(event.source instanceof GuiLabel)
		{
			int index = gui.controls.indexOf(event.source);
			
			if(index != -1)
			{
				if(selected != -1 && selected < gui.controls.size())
					((GuiLabel)gui.controls.get(selected)).color = 14737632;
				selected = index;
				((GuiLabel)gui.controls.get(selected)).color = 16777000;
				onSelectionChange();
			}
		}
	}
	
	public void onSelectionChange()
	{
		
	}
}
