package team.creative.creativecore.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(value = Dist.CLIENT)
public class GuiRenderHelper {
	
	private static final Minecraft mc = Minecraft.getInstance();
	
	public static void drawItemStack(MatrixStack matrix, ItemStack stack) {
		ItemRenderer renderer = mc.getItemRenderer();
		
		RenderSystem.pushMatrix();
		mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
		mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmapDirect(false, false);
		RenderSystem.enableRescaleNormal();
		RenderSystem.enableAlphaTest();
		RenderSystem.defaultAlphaFunc();
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		//RenderSystem.translated(x, y, 0);
		RenderSystem.translatef(8.0F, 8.0F, 0.0F);
		RenderSystem.scalef(1.0F, -1.0F, 1.0F);
		RenderSystem.scalef(16, 16, 16);
		IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
		IBakedModel bakedmodel = renderer.getItemModelWithOverrides(stack, (World) null, (LivingEntity) null);
		boolean flag = !bakedmodel.isSideLit();
		if (flag)
			RenderHelper.setupGuiFlatDiffuseLighting();
		matrix.push();
		Matrix4f m = matrix.getLast().getMatrix();
		Vector4f vec = new Vector4f();
		vec.setW(1);
		vec.transform(m);
		float shrink = 1 / 16F;
		m.translate(new Vector3f(-vec.getX() + vec.getX() * shrink, -vec.getY() - vec.getY() * shrink, -vec.getZ() + vec.getZ() * shrink));
		renderer.renderItem(stack, ItemCameraTransforms.TransformType.GUI, false, matrix, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
		matrix.pop();
		irendertypebuffer$impl.finish();
		RenderSystem.enableDepthTest();
		if (flag)
			RenderHelper.setupGui3DDiffuseLighting();
		
		RenderSystem.disableAlphaTest();
		RenderSystem.disableRescaleNormal();
		RenderSystem.popMatrix();
	}
	
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
