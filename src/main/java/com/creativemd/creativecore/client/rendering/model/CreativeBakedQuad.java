package com.creativemd.creativecore.client.rendering.model;

import java.util.Arrays;

import org.lwjgl.util.Color;

import com.creativemd.creativecore.common.utils.ColorUtils;
import com.creativemd.creativecore.common.utils.CubeObject;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class CreativeBakedQuad extends BakedQuad {
	
	public static CreativeBakedQuad lastRenderedQuad = null;
	public final CubeObject cube;
	
	public CreativeBakedQuad(BakedQuad quad, CubeObject cube, int tintedColor)
    {
        super(Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length), /*tintedColor*/quad.getTintIndex(), FaceBakery.getFacingFromVertexData(quad.getVertexData()), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat());
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
