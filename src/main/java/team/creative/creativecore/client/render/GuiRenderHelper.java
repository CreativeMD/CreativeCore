package team.creative.creativecore.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(value = Dist.CLIENT)
public class GuiRenderHelper {
	
	private static final Minecraft mc = Minecraft.getInstance();
	
	public static void drawStringCentered(MatrixStack stack, String text, int width, int height, int color, boolean shadow) {
		int textWidth = mc.fontRenderer.getStringWidth(text);
		if (textWidth > width) {
			int dotWith = mc.fontRenderer.getStringWidth("...");
			if (textWidth > dotWith) {
				StringBuilder builder = new StringBuilder();
				textWidth = 0;
				for (int i = 0; i < text.length(); i++) {
					int charWidth = mc.fontRenderer.getStringWidth("" + text.charAt(i));
					if (charWidth + textWidth + dotWith < width) {
						builder.append(text.charAt(i));
						textWidth += charWidth;
					} else
						break;
				}
				text = builder.toString() + "...";
			}
		}
		mc.fontRenderer.drawStringWithShadow(stack, text, width / 2 - mc.fontRenderer.getStringWidth(text) / 2, height / 2 - mc.fontRenderer.FONT_HEIGHT / 2, color);
	}
	
	public static void fillGradient(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int colorFrom, int colorTo) {
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.disableAlphaTest();
		RenderSystem.defaultBlendFunc();
		RenderSystem.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		fillGradient(matrixStack.getLast().getMatrix(), bufferbuilder, x1, y1, x2, y2, 0, colorFrom, colorTo);
		tessellator.draw();
		RenderSystem.shadeModel(7424);
		RenderSystem.disableBlend();
		RenderSystem.enableAlphaTest();
		RenderSystem.enableTexture();
	}
	
	public static void fillGradient(Matrix4f matrix, BufferBuilder builder, int x1, int y1, int x2, int y2, int z, int colorA, int colorB) {
		float f = (colorA >> 24 & 255) / 255.0F;
		float f1 = (colorA >> 16 & 255) / 255.0F;
		float f2 = (colorA >> 8 & 255) / 255.0F;
		float f3 = (colorA & 255) / 255.0F;
		float f4 = (colorB >> 24 & 255) / 255.0F;
		float f5 = (colorB >> 16 & 255) / 255.0F;
		float f6 = (colorB >> 8 & 255) / 255.0F;
		float f7 = (colorB & 255) / 255.0F;
		builder.pos(matrix, x2, y1, z).color(f1, f2, f3, f).endVertex();
		builder.pos(matrix, x1, y1, z).color(f1, f2, f3, f).endVertex();
		builder.pos(matrix, x1, y2, z).color(f5, f6, f7, f4).endVertex();
		builder.pos(matrix, x2, y2, z).color(f5, f6, f7, f4).endVertex();
	}
	
}
