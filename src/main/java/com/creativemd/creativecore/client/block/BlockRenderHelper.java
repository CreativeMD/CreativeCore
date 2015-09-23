package com.creativemd.creativecore.client.block;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.client.rendering.RenderHelper3D;
import com.creativemd.creativecore.common.utils.CubeObject;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

@SideOnly(Side.CLIENT)
public class BlockRenderHelper {
	
	public static void renderCubes(IBlockAccess world, ArrayList<CubeObject> cubes, int x, int y, int z, Block block, RenderBlocks renderer, ForgeDirection direction)
	{
		for (int i = 0; i < cubes.size(); i++) {
			renderer.setRenderBounds(cubes.get(i).minX, cubes.get(i).minY, cubes.get(i).minZ, cubes.get(i).maxX, cubes.get(i).maxY, cubes.get(i).maxZ);
			if(direction != null && direction != ForgeDirection.EAST && direction != ForgeDirection.UNKNOWN)
				RenderHelper3D.applyBlockRotation(renderer, direction);
			if(cubes.get(i).icon != null)
				renderer.setOverrideBlockTexture(cubes.get(i).icon);
			
			if(cubes.get(i).block != null)
				if(cubes.get(i).meta != -1)
				{
					
					RenderHelper3D.renderBlocks.clearOverrideBlockTexture();
					RenderHelper3D.renderBlocks.setRenderBounds(cubes.get(i).minX, cubes.get(i).minY, cubes.get(i).minZ, cubes.get(i).maxX, cubes.get(i).maxY, cubes.get(i).maxZ);
					RenderHelper3D.renderBlocks.meta = cubes.get(i).meta;
					IBlockAccessFake fake = new IBlockAccessFake(renderer.blockAccess);
					RenderHelper3D.renderBlocks.blockAccess = fake;
					fake.overrideMeta = cubes.get(i).meta;
					RenderHelper3D.renderBlocks.lockBlockBounds = true;
					RenderHelper3D.renderBlocks.renderBlockAllFaces(cubes.get(i).block, x, y, z);
					RenderHelper3D.renderBlocks.lockBlockBounds = false;
					continue;
				}
				else
					renderer.setOverrideBlockTexture(cubes.get(i).block.getBlockTextureFromSide(0));
			
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
			Block block = parBlock;
			renderer.setRenderBounds(cubes.get(i).minX, cubes.get(i).minY, cubes.get(i).minZ, cubes.get(i).maxX, cubes.get(i).maxY, cubes.get(i).maxZ);
			if(cubes.get(i).block != null)
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
				GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
				tesselator.startDrawingQuads();
				tesselator.setNormal(0.0F, -1.0F, 0.0F);
				renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, meta));
				tesselator.draw();
				tesselator.startDrawingQuads();
				tesselator.setNormal(0.0F, 1.0F, 0.0F);
				renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, meta));
				tesselator.draw();
				tesselator.startDrawingQuads();
				tesselator.setNormal(0.0F, 0.0F, -1.0F);
				renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, meta));
				tesselator.draw();
				tesselator.startDrawingQuads();
				tesselator.setNormal(0.0F, 0.0F, 1.0F);
				renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, meta));
				tesselator.draw();
				tesselator.startDrawingQuads();
				tesselator.setNormal(-1.0F, 0.0F, 0.0F);
				renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, meta));
				tesselator.draw();
				tesselator.startDrawingQuads();
				tesselator.setNormal(1.0F, 0.0F, 0.0F);
				renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, meta));
				tesselator.draw();
				GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			}
		}
	}
	    
	
}
