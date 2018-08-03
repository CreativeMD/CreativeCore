package com.creativemd.creativecore.client.rendering.model;

import java.util.Arrays;

import org.lwjgl.util.Color;

import com.creativemd.creativecore.client.mods.optifine.OptifineHelper;
import com.creativemd.creativecore.client.rendering.RenderCubeObject;
import com.creativemd.creativecore.common.utils.math.box.CubeObject;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.QuadGatheringTransformer;
import net.minecraftforge.client.model.pipeline.VertexLighterSmoothAo;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class CreativeBakedQuad extends BakedQuad {
	
	public static TextureAtlasSprite missingSprite = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getTextureMap().getMissingSprite();
	public static CreativeBakedQuad lastRenderedQuad = null;
	public final RenderCubeObject cube;
	public boolean shouldOverrideColor;
	
	public CreativeBakedQuad(EnumFacing facing)
    {
		super(new int[28], -1, facing, missingSprite, true, net.minecraft.client.renderer.vertex.DefaultVertexFormats.BLOCK);
		cube = null;
    }
	
	public CreativeBakedQuad(BakedQuad quad, RenderCubeObject cube, int tintedColor, boolean shouldOverrideColor, EnumFacing facing)
    {
        this(quad, cube, tintedColor, shouldOverrideColor, facing, false);
    }
	
	private CreativeBakedQuad(BakedQuad quad, RenderCubeObject cube, int tintedColor, boolean shouldOverrideColor, EnumFacing facing, boolean something)
	{
		super(copyArray(quad.getVertexData()), shouldOverrideColor ? tintedColor : quad.getTintIndex(), facing, quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat());
        this.cube = cube;
        this.shouldOverrideColor = shouldOverrideColor;
	}
	
	private static int[] copyArray(int[] array)
	{
		int[] newarray = new int[array.length];
		for (int i = 0; i < array.length; i++) {
			newarray[i] = array[i];
		}
		return newarray;
	}
	
	@Override
    public void pipe(net.minecraftforge.client.model.pipeline.IVertexConsumer consumer)
    {
		lastRenderedQuad = this;
        net.minecraftforge.client.model.pipeline.LightUtil.putBakedQuad(consumer, this);
        lastRenderedQuad = null;
    }
	
}
