package com.creativemd.creativecore.client.avatar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class Avatar {
	
	/**Handles the rendering of a RepresntiveObject, **/
	public abstract void handleRendering(Minecraft mc, FontRenderer fontRenderer, int width, int height);
	
}
