package com.creativemd.creativecore.client.block;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

import com.creativemd.creativecore.client.rendering.ExtendedRenderBlocks;
import com.creativemd.creativecore.client.rendering.RenderHelper3D;
import com.creativemd.creativecore.common.utils.ColorUtils;
import com.creativemd.creativecore.common.utils.CubeObject;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;


@SideOnly(Side.CLIENT)
public class BlockRenderHelper {
	
	public static IBlockAccessFake fake = null;
	
	public static void renderCubes(IBlockAccess world, ArrayList<CubeObject> cubes, int x, int y, int z, Block block, RenderBlocks renderer, ForgeDirection direction)
	{
		renderCubes(world, cubes, x, y, z, block, renderer, direction, RenderHelper3D.renderBlocks);
	}
	
	public static void renderCubes(IBlockAccess world, ArrayList<CubeObject> cubes, int x, int y, int z, Block block, RenderBlocks renderer, ForgeDirection direction, ExtendedRenderBlocks extraRenderer)
	{
		for (int i = 0; i < cubes.size(); i++) {
			
			
			if(cubes.get(i).icon != null)
				renderer.setOverrideBlockTexture(cubes.get(i).icon);
			
			if(cubes.get(i).block != null)
				if(cubes.get(i).meta != -1)
				{
					if(fake == null)
					{
						fake = new IBlockAccessFake(renderer.blockAccess);
						extraRenderer.blockAccess = fake;
					}
					
					if(fake.world != renderer.blockAccess)
						fake.world = renderer.blockAccess;
					
					extraRenderer.clearOverrideBlockTexture();
					extraRenderer.setRenderBounds(cubes.get(i).minX, cubes.get(i).minY, cubes.get(i).minZ, cubes.get(i).maxX, cubes.get(i).maxY, cubes.get(i).maxZ);
					extraRenderer.meta = cubes.get(i).meta;					
					fake.overrideMeta = cubes.get(i).meta;
					extraRenderer.color = cubes.get(i).color;
					extraRenderer.lockBlockBounds = true;
					extraRenderer.renderBlockAllFaces(cubes.get(i).block, x, y, z);
					extraRenderer.lockBlockBounds = false;
					extraRenderer.color = ColorUtils.WHITE;
					continue;
				}
				else
					renderer.setOverrideBlockTexture(cubes.get(i).block.getBlockTextureFromSide(0));
			
			renderer.setRenderBounds(cubes.get(i).minX, cubes.get(i).minY, cubes.get(i).minZ, cubes.get(i).maxX, cubes.get(i).maxY, cubes.get(i).maxZ);
			
			if(direction != null && direction != ForgeDirection.EAST && direction != ForgeDirection.UNKNOWN)
				RenderHelper3D.applyBlockRotation(renderer, direction);
			
			renderer.renderStandardBlock(block, x, y, z);
			
			if(cubes.get(i).icon != null || cubes.get(i).block != null)
				renderer.clearOverrideBlockTexture();
		}
	}
	
	public static void renderInventoryCubes(RenderBlocks renderer, ArrayList<CubeObject> cubes, Block parBlock, int meta)
	{
		Tessellator tesselator = Tessellator.instance;
		for (int i = 0; i < cubes.size(); i++)
		{
			/* if (block.getRenderBlockPass() != 0)
            {
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            }
            else
            {
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.5F);
                GL11.glDisable(GL11.GL_BLEND);
            }*/
			int metadata = 0;
			if(cubes.get(i).meta != -1)
				metadata = cubes.get(i).meta;
			Block block = parBlock;
			if(block instanceof BlockAir)
				block = Blocks.stone;
			renderer.setRenderBounds(cubes.get(i).minX, cubes.get(i).minY, cubes.get(i).minZ, cubes.get(i).maxX, cubes.get(i).maxY, cubes.get(i).maxZ);
			if(cubes.get(i).block != null && !(cubes.get(i).block instanceof BlockAir))
			{
				block = cubes.get(i).block;
				meta = 0;
			}
			if(cubes.get(i).icon != null){
				GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
				tesselator.startDrawingQuads();
				tesselator.setNormal(0.0F, -1.0F, 0.0F);
				renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, cubes.get(i).icon);
				tesselator.draw();
				tesselator.startDrawingQuads();
				tesselator.setNormal(0.0F, 1.0F, 0.0F);
				renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, cubes.get(i).icon);
				tesselator.draw();
				tesselator.startDrawingQuads();
				tesselator.setNormal(0.0F, 0.0F, -1.0F);
				renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, cubes.get(i).icon);
				tesselator.draw();
				tesselator.startDrawingQuads();
				tesselator.setNormal(0.0F, 0.0F, 1.0F);
				renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, cubes.get(i).icon);
				tesselator.draw();
				tesselator.startDrawingQuads();
				tesselator.setNormal(-1.0F, 0.0F, 0.0F);
				renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, cubes.get(i).icon);
				tesselator.draw();
				tesselator.startDrawingQuads();
				tesselator.setNormal(1.0F, 0.0F, 0.0F);
				renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, cubes.get(i).icon);
				tesselator.draw();
				GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			}else{
				int j = block.getRenderColor(metadata);
				if(cubes.get(i).color != ColorUtils.WHITE)
					j = cubes.get(i).color;
				/*boolean defaultColor = cubes.get(i).;
	            if (defaultColor)
	            {
	                j = 16777215;
	            }*/

	            float f1 = (float)(j >> 16 & 255) / 255.0F;
	            float f2 = (float)(j >> 8 & 255) / 255.0F;
	            float f3 = (float)(j & 255) / 255.0F;
	            float brightness = 1.0F;
	            GL11.glColor4f(f1 * brightness, f2 * brightness, f3 * brightness, 1.0F);
	            
				GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
				tesselator.startDrawingQuads();
				tesselator.setNormal(0.0F, -1.0F, 0.0F);
				renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, metadata));
				tesselator.draw();
				tesselator.startDrawingQuads();
				tesselator.setNormal(0.0F, 1.0F, 0.0F);
				renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, metadata));
				tesselator.draw();
				tesselator.startDrawingQuads();
				tesselator.setNormal(0.0F, 0.0F, -1.0F);
				renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, metadata));
				tesselator.draw();
				tesselator.startDrawingQuads();
				tesselator.setNormal(0.0F, 0.0F, 1.0F);
				renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, metadata));
				tesselator.draw();
				tesselator.startDrawingQuads();
				tesselator.setNormal(-1.0F, 0.0F, 0.0F);
				renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, metadata));
				tesselator.draw();
				tesselator.startDrawingQuads();
				tesselator.setNormal(1.0F, 0.0F, 0.0F);
				renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, metadata));
				tesselator.draw();
				GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			}
		}
	}
	    
	
}
