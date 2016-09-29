package com.creativemd.creativecore.client.rendering.model;

import java.lang.reflect.Array;
import java.util.ArrayList;

import com.creativemd.creativecore.common.utils.CubeObject;
import com.creativemd.creativecore.common.utils.RenderCubeObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.BlockInfo;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.QuadGatheringTransformer;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import net.minecraftforge.client.model.pipeline.VertexLighterSmoothAo;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class CreativeBakedQuadCaching extends CreativeBakedQuad {
	
	public ICustomCachedCreativeRendered cacher;
	public ArrayList<BakedQuad> quads;
	public TileEntity te;
	public ItemStack stack;
	public BlockRenderLayer layer;
	
	public CreativeBakedQuadCaching(ArrayList<BakedQuad> quads, EnumFacing facing, ICustomCachedCreativeRendered cacher, TileEntity te, ItemStack stack, BlockRenderLayer layer) {
		super(facing);
		this.quads = quads;
		this.cacher = cacher;
		this.te = te;
		this.stack = stack;
		this.layer = layer;
	}
	
	@Override
    public void pipe(net.minecraftforge.client.model.pipeline.IVertexConsumer consumer)
    {
		if(consumer instanceof VertexLighterSmoothAo)
		{
			VertexLighterSmoothAo smooth = (VertexLighterSmoothAo) consumer;
			IVertexConsumer parent = ReflectionHelper.getPrivateValue(QuadGatheringTransformer.class, smooth, "parent");
			BlockInfo info = ReflectionHelper.getPrivateValue(VertexLighterFlat.class, smooth, "blockInfo");
			
			//long time = System.nanoTime();
			CreativeConsumer cc = new CreativeConsumer(Minecraft.getMinecraft().getBlockColors());
			//if(CreativeConsumer.instance.getParent() != parent)
				cc.setParent(parent);
				
			
			
			cc.blockInfo = info;
			
			QuadCache[] cached = cacher.getCustomCachedQuads(layer, face, te, stack);
			if(cached != null)		
				cc.processCachedQuad(cached);
			else{
				cached = new QuadCache[quads.size()];
				for (int i = 0; i < quads.size(); i++) {
					BakedQuad quad = quads.get(i);
					lastRenderedQuad = (CreativeBakedQuad) quad;
					try{
						net.minecraftforge.client.model.pipeline.LightUtil.putBakedQuad(cc, quad);
					}catch(Exception e){
						//e.printStackTrace();
					}
					cached[i] = cc.lastCache;
					lastRenderedQuad = null;
				}
				cacher.saveCachedQuads(cached, layer, face, te, stack);
			}
		}else
			super.pipe(consumer);
    }

}
