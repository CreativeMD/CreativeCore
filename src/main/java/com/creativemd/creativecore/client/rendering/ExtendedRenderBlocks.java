package com.creativemd.creativecore.client.rendering;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class ExtendedRenderBlocks extends RenderBlocks{
	
	public int meta;
	
	public int side = -1;
	
	@Override
	public IIcon getBlockIcon(Block par1Block, IBlockAccess par2IBlockAccess, int par3, int par4, int par5, int par6)
    {
		if(side != -1) par6 = side;
        return this.getIconSafe(par1Block.getIcon(par6, meta));
    }
	
	public ExtendedRenderBlocks(RenderBlocks renderer)
	{
		super();
		this.blockAccess = renderer.blockAccess;
		this.overrideBlockTexture = renderer.overrideBlockTexture;
		this.flipTexture = renderer.flipTexture;
		this.field_152631_f = renderer.field_152631_f;
		this.renderAllFaces = renderer.renderAllFaces;
		this.fancyGrass = renderer.fancyGrass;
		this.useInventoryTint = renderer.useInventoryTint;
		this.renderFromInside = renderer.renderFromInside;
		this.enableAO = renderer.enableAO;
	}
	
}
