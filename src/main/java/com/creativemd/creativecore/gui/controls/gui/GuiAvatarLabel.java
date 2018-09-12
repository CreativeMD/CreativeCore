package com.creativemd.creativecore.gui.controls.gui;

import com.creativemd.creativecore.client.avatar.Avatar;

public class GuiAvatarLabel extends GuiAvatarLabelClickable {

	public GuiAvatarLabel(String name, String title, int x, int y, int color, Avatar avatar) {
		super(name, title, x, y, color, avatar);
	}

	public GuiAvatarLabel(String title, int x, int y, int color, Avatar avatar) {
		this(title, title, x, y, color, avatar);
	}

	@Override
	public void onClicked(int x, int y, int button) {
	}

}
