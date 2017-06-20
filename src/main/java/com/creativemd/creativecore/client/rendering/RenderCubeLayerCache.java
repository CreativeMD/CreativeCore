package com.creativemd.creativecore.client.rendering;

import java.util.ArrayList;

import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCubeLayerCache {
	
	private ArrayList<RenderCubeObject> solid;
	private ArrayList<RenderCubeObject> cutout_mipped;
	private ArrayList<RenderCubeObject> cutout;
	private ArrayList<RenderCubeObject> translucent;
	
	public ArrayList<RenderCubeObject> getCubesByLayer(BlockRenderLayer layer)
	{
		switch(layer)
		{
		case SOLID:
			return solid;
		case CUTOUT_MIPPED:
			return cutout_mipped;
		case CUTOUT:
			return cutout;
		case TRANSLUCENT:
			return translucent;
		}
		return null;
	}
	
	public void setCubesByLayer(ArrayList<RenderCubeObject> cubes, BlockRenderLayer layer)
	{
		switch(layer)
		{
		case SOLID:
			solid = cubes;
			break;
		case CUTOUT_MIPPED:
			cutout_mipped = cubes;
			break;
		case CUTOUT:
			cutout = cubes;
			break;
		case TRANSLUCENT:
			translucent = cubes;
			break;
		}
	}
	
	public boolean doesNeedUpdate()
	{
		return solid == null || cutout_mipped == null || cutout == null || translucent == null;
	}
	
	public void clearCache()
	{
		solid = null;
		cutout_mipped = null;
		cutout = null;
		translucent = null;
	}
	
}
