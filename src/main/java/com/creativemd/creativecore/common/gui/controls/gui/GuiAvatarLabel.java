package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.client.avatar.Avatar;

public class GuiAvatarLabel extends GuiAvatarLabelClickable {
    
    public GuiAvatarLabel(String name, String title, int x, int y, int color, Avatar avatar, boolean avatarLeft) {
        super(name, title, x, y, color, avatar, avatarLeft);
    }
    
    public GuiAvatarLabel(String title, int x, int y, int color, Avatar avatar) {
        this(title, title, x, y, color, avatar, true);
    }
    
    @Override
    public void onClicked(int x, int y, int button) {}
    
}
