package com.creativemd.creativecore.gui.controls.gui;

import java.util.ArrayList;

import com.creativemd.creativecore.gui.event.gui.GuiControlChangedEvent;
import com.creativemd.creativecore.gui.event.gui.GuiControlClickEvent;
import com.n247s.api.eventapi.eventsystem.CustomEventSubscribe;

import net.minecraft.entity.player.EntityPlayer;

public class GuiListBox extends GuiScrollBox{
	
	protected ArrayList<String> lines = new ArrayList<String>();
	
	public int selected = -1;
	
	public GuiListBox(String name, int x, int y, int width, int height, ArrayList<String> lines) {
		super(name, x, y, width, height);
		this.lines = lines;
		refreshControls();
	}
	
	public void clear()
	{
		lines.clear();
		selected = -1;
		maxScroll = 0;
		scrolled = 0;
		reloadControls();
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
		reloadControls();
	}
	
	public void remove(int id)
	{
		lines.remove(id);
		reloadControls();
	}
	
	public void reloadControls()
	{
		controls.clear();
		for (int i = 0; i < lines.size(); i++) {
			int color = 14737632;
			if(i == selected)
				color = 16777000;
			GuiClickableLabel label = new GuiClickableLabel(lines.get(i), 3, 1+i*15, width-20, 15, color) {
				
				@Override
				public void onClicked(int x, int y, int button) {
					int index = controls.indexOf(this);
					
					if(index != -1)
					{
						if(selected != -1 && selected < controls.size())
							((GuiLabel)controls.get(selected)).color = 14737632;
						selected = index;
						((GuiLabel)controls.get(selected)).color = 16777000;
						
						raiseEvent(new GuiControlChangedEvent(this));
						onSelectionChange();
					}
				}
			};
			controls.add(label);
		}
	}
	
	public void onSelectionChange()
	{
		
	}
}
