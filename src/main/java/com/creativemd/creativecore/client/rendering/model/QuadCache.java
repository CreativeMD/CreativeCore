package com.creativemd.creativecore.client.rendering.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class QuadCache {
	
	//public ArrayList<FloatCache> fields = new ArrayList<>();
	private int v = 0;
	public VertexFormat format;
	public final ArrayList<int[]> quadDatas = new ArrayList<>();
	private int[] quadData;
	
	public QuadCache(VertexFormat format) {
		this.format = format;
		
	}
	
	public void addCache(int index, float... cache/*, EnumUsage usage*/)
	{
		if(quadData == null)
			quadData = new int[format.getNextOffset()/* / 4 * 4 */];
        /*if(renderer.isColorDisabled() && format.getElement(index).getUsage() == EnumUsage.COLOR)
        {
        	cache = dummyColor;
        }*/
        LightUtil.pack(cache, quadData, format, v, index);
        if(index == format.getElementCount() - 1)
        {
            v++;
            if(v == 4)
            {
            	quadDatas.add(quadData);
            	quadData = null;
                //renderer.addVertexData(quadData);
                //renderer.putPosition(offset.getX(), offset.getY(), offset.getZ());
                //Arrays.fill(quadData, 0);
                v = 0;
            }
        }
		//fields.add(new FloatCache(index, cache/*, usage*/));
	}
	
	/*public static class FloatCache {
		
		public int index;
		public float[] cache;
		//public VertexFormatElement.EnumUsage usage;
		
		public FloatCache(int index, float[] cache)//, EnumUsage usage)
		{
			this.index = index;
			this.cache = Arrays.copyOf(cache, cache.length);
			//this.usage = usage;
		}
		
	}*/
}
