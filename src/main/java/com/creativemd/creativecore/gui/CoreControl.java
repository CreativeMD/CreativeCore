package com.creativemd.creativecore.gui;

import java.util.List;

import com.creativemd.creativecore.gui.container.IControlParent;
import com.creativemd.creativecore.gui.event.ControlEvent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;

public abstract class CoreControl {
	
	public IControlParent parent;
	public String name;
	
	public boolean enabled;
	
	private int id = -1;
	
	public void setID(int id) {
		this.id = id;
	}
	
	public int getID() {
		return id;
	}
	
	public CoreControl(String name) {
		this.name = name;
		this.enabled = true;
	}
	
	//================Construction================
	
	public CoreControl setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}
	
	//================Events================
	
	public void onOpened() {
	}
	
	public void onClosed() {
	}
	
	public void onTick() {
	}
	
	//================Various Helper Methods================
	
	public boolean is(String... name) {
		for (int i = 0; i < name.length; i++) {
			if (this.name.equalsIgnoreCase(name[i]))
				return true;
		}
		return false;
	}
	
	public boolean hasParent() {
		return parent != null;
	}
	
	public EntityPlayer getPlayer() {
		if (hasParent())
			return parent.getPlayer();
		return null;
	}
	
	public boolean isRemote() {
		return getPlayer().world.isRemote;
	}
	
	//================Internal Events================
	
	public boolean raiseEvent(ControlEvent event) {
		if (parent != null)
			return ((CoreControl) parent).raiseEvent(event);
		return false;
	}
	
	public void addListener(Object listener) {
		if (parent != null)
			((CoreControl) parent).addListener(listener);
	}
	
	public void removeListener(Object listener) {
		if (parent != null)
			((CoreControl) parent).removeListener(listener);
	}
	
	//================Interaction================
	
	public boolean isInteractable() {
		return enabled && (hasParent() ? ((CoreControl) parent).isInteractable() : true);
	}
	
	//================SORTING================
	
	public void moveControlAbove(CoreControl controlInBack) {
		if (hasParent())
			parent.moveControlAbove(this, controlInBack);
	}
	
	public void moveControlBehind(CoreControl controlInFront) {
		if (hasParent())
			parent.moveControlBehind(this, controlInFront);
	}
	
	public void moveControlToBottom() {
		if (hasParent())
			parent.moveControlToBottom(this);
	}
	
	public void moveControlToTop() {
		if (hasParent())
			parent.moveControlToTop(this);
	}
	
	//================Static Helpers================
	
	public static String translate(String text) {
		return I18n.translateToLocal(text);
	}
	
	public static List<String> translate(List<String> lines) {
		for (int i = 0; i < lines.size(); i++) {
			lines.set(i, translate(lines.get(i)));
		}
		return lines;
	}
	
}
