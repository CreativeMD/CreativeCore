package com.creativemd.creativecore.common.gui.controls;

import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.client.rendering.RenderHelper2D;
import com.creativemd.creativecore.client.rendering.RenderHelper3D;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.Items;
import net.minecraft.util.IIcon;

public abstract class GuiImageButtonControl extends GuiButtonControl{

	public GuiImageButtonControl(String caption, int x, int y, int width, int height, int id) {
		super(caption, x, y, width, height, id);
	}
	
	public abstract IIcon getIcon();
	
	@Override
	public void drawControl(FontRenderer renderer) {
		super.drawControl(renderer);
		IIcon icon = getIcon();
		RenderHelper2D.renderIcon(icon, width/2-8, height/2-8, 1, true, rotation, 16, 16);
	}
}
