package com.creativemd.creativecore.common.gui.controls.gui;

import java.util.ArrayList;
import java.util.List;

import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;

public class GuiListBoxBase<T extends GuiControl> extends GuiScrollBox {
	
	protected List<T> content;
	protected List<GuiButtonRemove> removeButtons;
	
	public final boolean modifiable;
	public int space = 2;
	
	public GuiListBoxBase(String name, int x, int y, int width, int height, boolean modifiable, List<T> entries) {
		super(name, x, y, width, height);
		this.content = entries;
		this.modifiable = modifiable;
		if (modifiable)
			removeButtons = new ArrayList<>();
		for (int i = 0; i < entries.size(); i++) {
			addControl(entries.get(i));
			if (modifiable) {
				GuiButtonRemove button = new GuiButtonRemove(i);
				addControl(button);
				removeButtons.add(button);
			}
		}
		reloadPositions();
	}
	
	public void reloadPositions() {
		int y = space / 2;
		for (int i = 0; i < content.size(); i++) {
			GuiControl control = content.get(i);
			if (modifiable) {
				GuiButtonRemove button = removeButtons.get(i);
				button.index = i;
				button.posY = y + 3;
			}
			
			control.posY = y;
			y += control.height + space;
		}
	}
	
	public void remove(int index) {
		removeControl(content.get(index));
		content.remove(index);
		if (modifiable) {
			removeControl(removeButtons.get(index));
			removeButtons.remove(index);
		}
		reloadPositions();
		
		raiseEvent(new GuiControlChangedEvent(this));
	}
	
	public void clear() {
		for (int i = 0; i < content.size(); i++) {
			removeControl(content.get(i));
			if (modifiable)
				removeControl(removeButtons.get(i));
		}
		
		content.clear();
		if (modifiable)
			removeButtons.clear();
	}
	
	public void addAll(List<T> entries) {
		for (T entry : entries) {
			content.add(entry);
			addControl(entry);
			if (modifiable) {
				GuiButtonRemove button = new GuiButtonRemove(content.size() - 1);
				addControl(button);
				removeButtons.add(button);
			}
		}
		
		reloadPositions();
	}
	
	public void add(T entry) {
		content.add(entry);
		addControl(entry);
		if (modifiable) {
			GuiButtonRemove button = new GuiButtonRemove(content.size() - 1);
			addControl(button);
			removeButtons.add(button);
		}
		
		if (content.size() == 1)
			reloadPositions();
		else {
			GuiControl before = content.get(content.size() - 2);
			entry.posY = before.posY + before.height + space;
			
			if (modifiable)
				removeButtons.get(removeButtons.size() - 1).posY = before.posY + before.height + space + 3;
		}
		
		raiseEvent(new GuiControlChangedEvent(this));
	}
	
	public boolean isEmpty() {
		return content.isEmpty();
	}
	
	public int size() {
		return content.size();
	}
	
	public T get(int index) {
		return content.get(index);
	}
	
	public class GuiButtonRemove extends GuiButton {
		
		public int index;
		
		public GuiButtonRemove(int index) {
			super("x", GuiListBoxBase.this.width - 25, 0, 8, 8);
			this.index = index;
		}
		
		@Override
		public void onClicked(int x, int y, int button) {
			GuiListBoxBase.this.remove(index);
		}
		
	}
	
}
