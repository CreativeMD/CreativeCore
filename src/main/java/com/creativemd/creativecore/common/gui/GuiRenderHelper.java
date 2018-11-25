package com.creativemd.creativecore.common.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

import com.creativemd.creativecore.common.utils.mc.ColorUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiRenderHelper {
	
	public static GuiRenderHelper instance = new GuiRenderHelper(Minecraft.getMinecraft());
	
	public FontRenderer font;
	public RenderItem itemRenderer;
	public static Minecraft mc = Minecraft.getMinecraft();
	
	public GuiRenderHelper(Minecraft mc) {
		this(mc.fontRenderer, mc.getRenderItem());
	}
	
	public GuiRenderHelper(FontRenderer font, RenderItem itemRenderer) {
		this.font = font;
		this.itemRenderer = itemRenderer;
	}
	
	public void drawGrayBackgroundRect(int x, int y, int width, int height) {
		int alpha = 180;
		
		int widthThird = width / 3;
		int heightThird = height / 3;
		// Color black = new Color(0, 0, 0, 150);
		// renderColorPlate(x, y, black, width, height);
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		
		vertexbuffer.pos((double) x + widthThird, (double) y, 0).color(0, 0, 0, 0).endVertex();
		vertexbuffer.pos((double) x, (double) y, 0).color(0, 0, 0, 0).endVertex();
		vertexbuffer.pos((double) x, (double) y + heightThird, 0).color(0, 0, 0, 0).endVertex();
		vertexbuffer.pos((double) x + widthThird, (double) y + heightThird, 0).color(0, 0, 0, alpha).endVertex();
		
		vertexbuffer.pos((double) x + widthThird, (double) y + heightThird, 0).color(0, 0, 0, alpha).endVertex();
		vertexbuffer.pos((double) x, (double) y + heightThird, 0).color(0, 0, 0, 0).endVertex();
		vertexbuffer.pos((double) x, (double) y + heightThird * 2, 0).color(0, 0, 0, 0).endVertex();
		vertexbuffer.pos((double) x + widthThird, (double) y + heightThird * 2, 0).color(0, 0, 0, alpha).endVertex();
		
		vertexbuffer.pos((double) x + widthThird, (double) y + heightThird * 3, 0).color(0, 0, 0, 0).endVertex();
		vertexbuffer.pos((double) x + widthThird, (double) y + heightThird * 2, 0).color(0, 0, 0, alpha).endVertex();
		vertexbuffer.pos((double) x, (double) y + heightThird * 2, 0).color(0, 0, 0, 0).endVertex();
		vertexbuffer.pos((double) x, (double) y + heightThird * 3, 0).color(0, 0, 0, 0).endVertex();
		
		vertexbuffer.pos((double) x + widthThird * 2, (double) y, 0).color(0, 0, 0, 0).endVertex();
		vertexbuffer.pos((double) x + widthThird, (double) y, 0).color(0, 0, 0, 0).endVertex();
		vertexbuffer.pos((double) x + widthThird, (double) y + heightThird, 0).color(0, 0, 0, alpha).endVertex();
		vertexbuffer.pos((double) x + widthThird * 2, (double) y + heightThird, 0).color(0, 0, 0, alpha).endVertex();
		
		vertexbuffer.pos((double) x + widthThird * 2, (double) y + heightThird, 0).color(0, 0, 0, alpha).endVertex();
		vertexbuffer.pos((double) x + widthThird, (double) y + heightThird, 0).color(0, 0, 0, alpha).endVertex();
		vertexbuffer.pos((double) x + widthThird, (double) y + heightThird * 2, 0).color(0, 0, 0, alpha).endVertex();
		vertexbuffer.pos((double) x + widthThird * 2, (double) y + heightThird * 2, 0).color(0, 0, 0, alpha).endVertex();
		
		vertexbuffer.pos((double) x + widthThird * 2, (double) y + heightThird * 2, 0).color(0, 0, 0, alpha).endVertex();
		vertexbuffer.pos((double) x + widthThird, (double) y + heightThird * 2, 0).color(0, 0, 0, alpha).endVertex();
		vertexbuffer.pos((double) x + widthThird, (double) y + heightThird * 3, 0).color(0, 0, 0, 0).endVertex();
		vertexbuffer.pos((double) x + widthThird * 2, (double) y + heightThird * 3, 0).color(0, 0, 0, 0).endVertex();
		
		vertexbuffer.pos((double) x + widthThird * 2, (double) y, 0).color(0, 0, 0, 0).endVertex();
		vertexbuffer.pos((double) x + widthThird * 2, (double) y + heightThird, 0).color(0, 0, 0, alpha).endVertex();
		vertexbuffer.pos((double) x + widthThird * 3, (double) y + heightThird, 0).color(0, 0, 0, 0).endVertex();
		vertexbuffer.pos((double) x + widthThird * 3, (double) y, 0).color(0, 0, 0, 0).endVertex();
		
		vertexbuffer.pos((double) x + widthThird * 3, (double) y + heightThird, 0).color(0, 0, 0, 0).endVertex();
		vertexbuffer.pos((double) x + widthThird * 2, (double) y + heightThird, 0).color(0, 0, 0, alpha).endVertex();
		vertexbuffer.pos((double) x + widthThird * 2, (double) y + heightThird * 2, 0).color(0, 0, 0, alpha).endVertex();
		vertexbuffer.pos((double) x + widthThird * 3, (double) y + heightThird * 2, 0).color(0, 0, 0, 0).endVertex();
		
		vertexbuffer.pos((double) x + widthThird * 3, (double) y + heightThird * 2, 0).color(0, 0, 0, 0).endVertex();
		vertexbuffer.pos((double) x + widthThird * 2, (double) y + heightThird * 2, 0).color(0, 0, 0, alpha).endVertex();
		vertexbuffer.pos((double) x + widthThird * 2, (double) y + heightThird * 3, 0).color(0, 0, 0, 0).endVertex();
		vertexbuffer.pos((double) x + widthThird * 3, (double) y + heightThird * 3, 0).color(0, 0, 0, 0).endVertex();
		
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}
	
	public void drawHorizontalChannelMaskGradientRect(int left, int top, int right, int bottom, int colour, int mask) {
		drawHorizontalGradientRect(left, top, right, bottom, (colour & ~mask) | 0xFF000000, colour | 0xFF000000 | mask);
	}
	
	public void drawHorizontalGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
		float f = (float) (startColor >> 24 & 255) / 255.0F;
		float f1 = (float) (startColor >> 16 & 255) / 255.0F;
		float f2 = (float) (startColor >> 8 & 255) / 255.0F;
		float f3 = (float) (startColor & 255) / 255.0F;
		float f4 = (float) (endColor >> 24 & 255) / 255.0F;
		float f5 = (float) (endColor >> 16 & 255) / 255.0F;
		float f6 = (float) (endColor >> 8 & 255) / 255.0F;
		float f7 = (float) (endColor & 255) / 255.0F;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		vertexbuffer.pos((double) right, (double) top, 0).color(f5, f6, f7, f4).endVertex();
		vertexbuffer.pos((double) left, (double) top, 0).color(f1, f2, f3, f).endVertex();
		vertexbuffer.pos((double) left, (double) bottom, 0).color(f1, f2, f3, f).endVertex();
		vertexbuffer.pos((double) right, (double) bottom, 0).color(f5, f6, f7, f4).endVertex();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}
	
	public void drawTexturedModalRect(ResourceLocation location, int x, int y, int textureX, int textureY, int width, int height) {
		this.mc.getTextureManager().bindTexture(location);
		drawTexturedModalRect(x, y, textureX, textureY, width, height);
	}
	
	public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double) (x + 0), (double) (y + height), (double) 0).tex((double) ((float) (textureX + 0) * 0.00390625F), (double) ((float) (textureY + height) * 0.00390625F)).endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + height), (double) 0).tex((double) ((float) (textureX + width) * 0.00390625F), (double) ((float) (textureY + height) * 0.00390625F)).endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + 0), (double) 0).tex((double) ((float) (textureX + width) * 0.00390625F), (double) ((float) (textureY + 0) * 0.00390625F)).endVertex();
		bufferbuilder.pos((double) (x + 0), (double) (y + 0), (double) 0).tex((double) ((float) (textureX + 0) * 0.00390625F), (double) ((float) (textureY + 0) * 0.00390625F)).endVertex();
		tessellator.draw();
	}
	
	public void renderColorPlate(Color color, int width, int height) {
		renderColorPlate(0, 0, color, width, height);
	}
	
	public void renderColorPlate(int x, int y, Color color, int width, int height) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
		bufferbuilder.pos((double) x, (double) height, 0.0D).endVertex();
		bufferbuilder.pos((double) width, (double) height, 0.0D).endVertex();
		bufferbuilder.pos((double) width, (double) y, 0.0D).endVertex();
		bufferbuilder.pos((double) x, (double) y, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}
	
	public static void renderColorTriangle(int x1, int y1, int x2, int y2, int x3, int y3, Color color) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
		// GlStateManager.color(1, 1, 1, 1);
		vertexbuffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION);
		vertexbuffer.pos((double) x1, (double) y1, 0.0D).endVertex();
		vertexbuffer.pos((double) y3, (double) y3, 0.0D).endVertex();
		vertexbuffer.pos((double) x2, (double) y2, 0.0D).endVertex();
		// vertexbuffer.pos((double)x1, (double)y3, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}
	
	public static void renderBakedQuads(List<BakedQuad> baked) {
		mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableRescaleNormal();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.pushMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.translate(-0.5F, -0.5F, -0.5F);
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.ITEM);
		for (int i = 0; i < baked.size(); i++) {
			int tint = baked.get(i).getTintIndex();
			if (tint == 0)
				tint = -1;
			else {
				Color color = ColorUtils.IntToRGBA(tint);
				color.setAlpha(255);
				tint = ColorUtils.RGBAToInt(color);
			}
			net.minecraftforge.client.model.pipeline.LightUtil.renderQuadColor(vertexbuffer, baked.get(i), tint);
		}
		tessellator.draw();
		
		GlStateManager.popMatrix();
		
		GlStateManager.cullFace(GlStateManager.CullFace.BACK);
		GlStateManager.popMatrix();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
		mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
	}
	
	public int getFontHeight() {
		return font.FONT_HEIGHT;
	}
	
	public int getStringWidth(String text) {
		return font.getStringWidth(text);
	}
	
	public int drawStringWithShadow(String text, int width, int height, int color) {
		return drawStringWithShadow(text, 0, 0, width, height, color);
	}
	
	public int drawStringWithShadow(String text, int x, int y, int width, int height, int color) {
		return drawStringWithShadow(text, x, y, width, height, color, 0);
	}
	
	public int drawStringWithShadow(String text, int x, int y, int width, int height, int color, int additionalWidth) {
		int completeWidth = font.getStringWidth(text) + additionalWidth;
		font.drawStringWithShadow(text, width / 2 - completeWidth / 2 + additionalWidth, height / 2 - getFontHeight() / 2, color);
		return completeWidth;
	}
	
	public void drawItemStackAndOverlay(ItemStack stack, int x, int y, int width, int height) {
		drawItemStack(stack, x, y, width, height);
		GlStateManager.pushMatrix();
		if (String.valueOf(stack.getCount()).length() > 3) {
			String s = String.valueOf(stack.getCount());
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			GlStateManager.disableBlend();
			int fontWidth = font.getStringWidth(s);
			GlStateManager.translate(font.FONT_HEIGHT / 2, fontWidth / 2, 0);
			GlStateManager.scale(0.5, 0.5, 0.5);
			GlStateManager.translate(-font.FONT_HEIGHT / 2, -fontWidth / 2, 0);
			fontWidth /= 2;
			font.drawStringWithShadow(s, (float) (x + 19 - 2 - fontWidth), (float) (y + 6 + 5), 16777215);
			
			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
			GlStateManager.enableBlend();
			itemRenderer.renderItemOverlayIntoGUI(font, stack, x, y, "");
		} else
			itemRenderer.renderItemOverlays(font, stack, x, y);
		
		GlStateManager.disableLighting();
		// GlStateManager.enableDepth();
		GlStateManager.popMatrix();
		
	}
	
	public void drawItemStack(ItemStack stack, int x, int y, int width, int height) {
		drawItemStack(stack, x, y, width, height, 0);
	}
	
	public void drawItemStack(ItemStack stack, int x, int y, int width, int height, int rotation) {
		if (stack.isEmpty())
			return;
		GlStateManager.pushMatrix();
		
		/* GlStateManager.enableRescaleNormal(); GlStateManager.enableBlend(); int k =
		 * 240; int l = 240;
		 * OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)k,
		 * (float)l);
		 * GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
		 * GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
		 * GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO); */
		RenderHelper.enableGUIStandardItemLighting();
		
		GlStateManager.translate(x + 8, y + 8, 0);
		GlStateManager.rotate(rotation, 0, 0, 1);
		GlStateManager.scale(width / 16D, height / 16D, 1);
		GlStateManager.translate(-8, -8, -itemRenderer.zLevel - 50);
		// GlStateManager.disableDepth();
		GlStateManager.enableDepth();
		itemRenderer.renderItemAndEffectIntoGUI(stack, 0, 0);
		GlStateManager.disableDepth();
		// GlStateManager.enableDepth();
		// GlStateManager.enableLighting();
		// RenderHelper.disableStandardItemLighting();
		GlStateManager.popMatrix();
		
	}
}
