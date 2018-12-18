package com.creativemd.creativecore.common.gui.client.style;

import javax.vecmath.Vector4f;

import com.creativemd.creativecore.common.utils.mc.ColorUtils;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ColoredTextureDisplayStyle extends TextureDisplayStyle {
	
	public Vector4f color;
	
	public ColoredTextureDisplayStyle(int color, ResourceLocation location, int u, int v) {
		super(location, u, v);
		this.color = new Vector4f(ColorUtils.getRed(color) / 255F, ColorUtils.getGreen(color) / 255F, ColorUtils.getBlue(color) / 255F, ColorUtils.getAlpha(color) / 255F);
	}
	
	protected void colorize() {
		GlStateManager.color(color.x, color.y, color.z, color.w);
	}
	
}
