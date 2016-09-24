package com.creativemd.creativecore.client.rendering.model;

import java.util.Arrays;

import org.lwjgl.util.Color;

import com.creativemd.creativecore.common.utils.ColorUtils;
import com.creativemd.creativecore.common.utils.CubeObject;
import com.creativemd.creativecore.common.utils.RenderCubeObject;

import net.minecraft.client.Minecraft;
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
	
	public CreativeBakedQuad(EnumFacing facing)
    {
		super(new int[28], -1, facing, missingSprite, true, net.minecraft.client.renderer.vertex.DefaultVertexFormats.BLOCK);
		cube = null;
    }
	
	public CreativeBakedQuad(BakedQuad quad, RenderCubeObject cube, int tintedColor, boolean shouldOverrideColor, EnumFacing facing)
    {
        super(Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length), shouldOverrideColor ? tintedColor : quad.getTintIndex(), facing, quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat());
        this.cube = cube;
    }
	
	@Override
    public void pipe(net.minecraftforge.client.model.pipeline.IVertexConsumer consumer)
    {
		lastRenderedQuad = this;
        net.minecraftforge.client.model.pipeline.LightUtil.putBakedQuad(consumer, this);
        lastRenderedQuad = null;
    }
	
}
