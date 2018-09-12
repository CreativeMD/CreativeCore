package com.creativemd.creativecore.client.avatar;

import com.creativemd.creativecore.gui.GuiRenderHelper;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class Avatar {

	/** Handles the rendering of a RepresntiveObject, **/
	public abstract void handleRendering(GuiRenderHelper helper, int width, int height);

}
