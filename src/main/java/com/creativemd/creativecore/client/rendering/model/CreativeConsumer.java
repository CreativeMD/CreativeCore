package com.creativemd.creativecore.client.rendering.model;

import java.lang.reflect.Field;

import javax.vecmath.Vector3f;

import com.creativemd.creativecore.client.mods.optifine.OptifineHelper;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.pipeline.BlockInfo;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.VertexBufferConsumer;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import net.minecraftforge.client.model.pipeline.VertexLighterSmoothAo;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class CreativeConsumer extends VertexLighterSmoothAo {
	
	public static CreativeConsumer instance = new CreativeConsumer(Minecraft.getMinecraft().getBlockColors());
	
	private static Field tint = ReflectionHelper.findField(VertexLighterFlat.class, "tint");
	private static Field diffuse = ReflectionHelper.findField(VertexLighterFlat.class, "diffuse");
	private static Field renderer = ReflectionHelper.findField(VertexBufferConsumer.class, "renderer");
	private static Field offset = ReflectionHelper.findField(VertexBufferConsumer.class, "offset");
	
	public CreativeConsumer(BlockColors colors) {
		super(colors);
	}
	
	public BlockInfo blockInfo;
	public boolean shouldOverrideColor = false;
	
	public IBlockState state;
	
	public IVertexConsumer getParent()
	{
		return parent;
	}
	
	public static void processCachedQuad(IVertexConsumer parent, QuadCache[] cached)
	{
		try {
			BlockPos pos = (BlockPos) offset.get(parent);
			VertexBuffer buffer = (VertexBuffer) renderer.get(parent);
			for(int i = 0; i < cached.length; i++)
			{
				
				for (int j = 0; j < cached[i].quadDatas.size(); j++) {
					buffer.addVertexData(cached[i].quadDatas.get(j));
					//parent.put(cached[i].quadDatas.get(j).index, cached[i].fields.get(j).cache);
					buffer.putPosition(pos.getX(), pos.getY(), pos.getZ());
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public QuadCache lastCache = null;
	
	@Override
    protected void updateLightmap(float[] normal, float[] lightmap, float x, float y, float z)
    {
        lightmap[0] = calcLightmap(blockInfo.getBlockLight(), x, y, z);
        lightmap[1] = calcLightmap(blockInfo.getSkyLight(), x, y, z);
    }
	
	@Override
	protected float getAo(float x, float y, float z)
    {
        int sx = x < 0 ? 1 : 2;
        int sy = y < 0 ? 1 : 2;
        int sz = z < 0 ? 1 : 2;

        if(x < 0) x++;
        if(y < 0) y++;
        if(z < 0) z++;

        float a = 0;
        float[][][] ao = blockInfo.getAo();
        a += ao[sx - 1][sy - 1][sz - 1] * (1 - x) * (1 - y) * (1 - z);
        a += ao[sx - 1][sy - 1][sz - 0] * (1 - x) * (1 - y) * (0 + z);
        a += ao[sx - 1][sy - 0][sz - 1] * (1 - x) * (0 + y) * (1 - z);
        a += ao[sx - 1][sy - 0][sz - 0] * (1 - x) * (0 + y) * (0 + z);
        a += ao[sx - 0][sy - 1][sz - 1] * (0 + x) * (1 - y) * (1 - z);
        a += ao[sx - 0][sy - 1][sz - 0] * (0 + x) * (1 - y) * (0 + z);
        a += ao[sx - 0][sy - 0][sz - 1] * (0 + x) * (0 + y) * (1 - z);
        a += ao[sx - 0][sy - 0][sz - 0] * (0 + x) * (0 + y) * (0 + z);

        a = MathHelper.clamp(a, 0, 1);
        return a;
    }

    @Override
    public void updateBlockInfo()
    {
        blockInfo.updateShift(false);
        blockInfo.updateLightMatrix();
    }
	
	@Override
    protected void processQuad()
    {
        float[][] position = quadData[posIndex];
        float[][] normal = null;
        float[][] lightmap = quadData[lightmapIndex];
        float[][] color = quadData[colorIndex];

        if(normalIndex != -1 && (
            quadData[normalIndex][0][0] != -1 ||
            quadData[normalIndex][0][1] != -1 ||
            quadData[normalIndex][0][2] != -1))
        {
            normal = quadData[normalIndex];
        }
        else
        {
            normal = new float[4][4];
            Vector3f v1 = new Vector3f(position[3]);
            Vector3f t = new Vector3f(position[1]);
            Vector3f v2 = new Vector3f(position[2]);
            v1.sub(t);
            t.set(position[0]);
            v2.sub(t);
            v1.cross(v2, v1);
            v1.normalize();
            for(int v = 0; v < 4; v++)
            {
                normal[v][0] = v1.x;
                normal[v][1] = v1.y;
                normal[v][2] = v1.z;
                normal[v][3] = 0;
            }
        }

        int multiplier = -1;
        
        int parentTint = -1;
		try {
			parentTint = tint.getInt(this);
		} catch (IllegalArgumentException | IllegalAccessException e2) {
			e2.printStackTrace();
		}
		//parentTint = 1;
        if(parentTint != -1 && !shouldOverrideColor)
        {
            //multiplier = blockInfo.getColorMultiplier(parentTint);
        	multiplier = Minecraft.getMinecraft().getBlockColors().colorMultiplier(state, blockInfo.getWorld(), blockInfo.getBlockPos(), parentTint);
        }else
        	multiplier = parentTint;

        VertexFormat format = parent.getVertexFormat();
        int count = format.getElementCount();
        boolean diffuseCache = false;
        try {
			diffuseCache = diffuse.getBoolean(this);
		} catch (IllegalArgumentException | IllegalAccessException e2) {
			e2.printStackTrace();
		}
        
        QuadCache cache = new QuadCache(format);

        for(int v = 0; v < 4; v++)
        {
            position[v][0] += blockInfo.getShx();
            position[v][1] += blockInfo.getShy();
            position[v][2] += blockInfo.getShz();

            float x = position[v][0] - .5f;
            float y = position[v][1] - .5f;
            float z = position[v][2] - .5f;

            //if(blockInfo.getBlock().isFullCube())
            {
                x += normal[v][0] * .5f;
                y += normal[v][1] * .5f;
                z += normal[v][2] * .5f;
            }

            float blockLight = lightmap[v][0], skyLight = lightmap[v][1];
            updateLightmap(normal[v], lightmap[v], x, y, z);
            if(dataLength[lightmapIndex] > 1)
            {
                if(blockLight > lightmap[v][0]) lightmap[v][0] = blockLight;
                if(skyLight > lightmap[v][1]) lightmap[v][1] = skyLight;
            }
            updateColor(normal[v], color[v], x, y, z, parentTint, multiplier);
			if(diffuseCache)
			{
			    float d = LightUtil.diffuseLight(normal[v][0], normal[v][1], normal[v][2]);
			    for(int i = 0; i < 3; i++)
			    {
			        color[v][i] *= d;
			    }
			}
            if(EntityRenderer.anaglyphEnable)
            {
                applyAnaglyph(color[v]);
            }

            
            // no need for remapping cause all we could've done is add 1 element to the end
            for(int e = 0; e < count; e++)
            {
                VertexFormatElement element = format.getElement(e);
                switch(element.getUsage())
                {
                    case POSITION:
                        // position adding moved to VertexBufferConsumer due to x and z not fitting completely into a float
                        /*float[] pos = new float[4];
                        System.arraycopy(position[v], 0, pos, 0, position[v].length);
                        pos[0] += blockInfo.getBlockPos().getX();
                        pos[1] += blockInfo.getBlockPos().getY();
                        pos[2] += blockInfo.getBlockPos().getZ();*/
                    	cache.addCache(e, position[v]);
                        parent.put(e, position[v]);
                        break;
                    case NORMAL: if(normalIndex != -1)
                    {
                    	cache.addCache(e, normal[v]);
                        parent.put(e, normal[v]);
                        break;
                    }
                    case COLOR:
                    	cache.addCache(e, color[v]);
                        parent.put(e, color[v]);
                        break;
                    case UV: if(element.getIndex() == 1)
                    {
                    	cache.addCache(e, lightmap[v]);
                        parent.put(e, lightmap[v]);
                        break;
                    }
                    default:
                    	cache.addCache(e, quadData[e][v]);
                        parent.put(e, quadData[e][v]);
                }
            }
        }
        lastCache = cache;
        setQuadTint(-1);
        
        
        //tint = -1;
    }
}
