package com.creativemd.creativecore.client.rendering.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class QuadCache {
	
	public ArrayList<FloatCache> fields = new ArrayList<>();
	
	public void addCache(int index, float[] cache/*, EnumUsage usage*/)
	{
		fields.add(new FloatCache(index, cache/*, usage*/));
	}
	
	public static class FloatCache {
		
		public int index;
		public float[] cache;
		//public VertexFormatElement.EnumUsage usage;
		
		public FloatCache(int index, float[] cache)//, EnumUsage usage)
		{
			this.index = index;
			this.cache = Arrays.copyOf(cache, cache.length);
			//this.usage = usage;
		}
		
	}
}
