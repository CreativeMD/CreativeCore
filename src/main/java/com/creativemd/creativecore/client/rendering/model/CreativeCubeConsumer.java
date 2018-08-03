package com.creativemd.creativecore.client.rendering.model;

import java.util.Arrays;

import javax.vecmath.Vector3f;

import org.lwjgl.util.Color;

import com.creativemd.creativecore.client.mods.optifine.OptifineHelper;
import com.creativemd.creativecore.client.rendering.RenderCubeObject;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.pipeline.BlockInfo;
import net.minecraftforge.client.model.pipeline.LightUtil;

public class CreativeCubeConsumer {
	
	public final VertexFormat format;
	protected int vertices = 0;
	
    protected byte[] dataLength = null;
    protected float[][][] quadData = null;
    
    protected final BlockInfo blockInfo;
    private int tint = -1;
    private boolean diffuse = true;

    protected int posIndex = -1;
    protected int normalIndex = -1;
    protected int colorIndex = -1;
    protected int lightmapIndex = -1;
    
    public BakedQuad quad;
    public RenderCubeObject cube;
    public BufferBuilder buffer;
    public BlockRenderLayer layer;
    public IBlockState state;
    
    private final CreativeVertexBufferConsumer parent;
    
    public BlockInfo getBlockInfo()
    {
    	return blockInfo;
    }
    
    public CreativeCubeConsumer(VertexFormat newFormat, BlockColors colors) {
		this.format = new VertexFormat(newFormat);
		this.blockInfo = new BlockInfo(colors);
        for(int i = 0; i < format.getElementCount(); i++)
        {
            switch(format.getElement(i).getUsage())
            {
                case POSITION:
                    posIndex = i;
                    break;
                case NORMAL:
                    normalIndex = i;
                    break;
                case COLOR:
                    colorIndex = i;
                    break;
                case UV:
                    if(format.getElement(i).getIndex() == 1)
                    {
                        lightmapIndex = i;
                    }
                    break;
                default:
            }
        }
        if(posIndex == -1)
        {
            throw new IllegalArgumentException("vertex lighter needs format with position");
        }
        if(lightmapIndex == -1)
        {
            throw new IllegalArgumentException("vertex lighter needs format with lightmap");
        }
        if(colorIndex == -1)
        {
            throw new IllegalArgumentException("vertex lighter needs format with color");
        }
        parent = new CreativeVertexBufferConsumer(this);
        dataLength = new byte[format.getElementCount()];
        quadData = new float[format.getElementCount()][4][4];
	}
    
    public void put(int element, float... data)
    {
        System.arraycopy(data, 0, quadData[element][vertices], 0, data.length);
        if(element == format.getElementCount() - 1) vertices++;
        if(vertices == 0)
        {
            dataLength[element] = (byte)data.length;
        }
        else if(vertices == 4)
        {
            vertices = 0;
            processQuad();
        }
    }
    
    protected void processQuad()
    {
        float[][] position = quadData[posIndex];
        float[][] normal = null;
        float[][] lightmap = quadData[lightmapIndex];
        float[][] color = quadData[colorIndex];

        /*if(normalIndex != -1 && (
            quadData[normalIndex][0][0] != -1 ||
            quadData[normalIndex][0][1] != -1 ||
            quadData[normalIndex][0][2] != -1))
        {
            normal = quadData[normalIndex];
        }
        else
        {*/
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
        //}

        int multiplier = -1;
        
        if(OptifineHelper.isActive())
        {
        	multiplier = OptifineHelper.getColorMultiplier(quad, state, blockInfo.getWorld(), blockInfo.getBlockPos());
        	if(multiplier != -1)
        		tint = 0;
        }
        if(tint != -1 && (cube.color == -1 || ColorUtils.isWhite(cube.color)))
        {
        	if(multiplier == -1)
        		multiplier = blockInfo.getColorMultiplier(tint);
        	
            Color tempColor = ColorUtils.IntToRGBA(multiplier);
            if(cube.color != -1)
            	tempColor.setAlpha(ColorUtils.getAlpha(cube.color));
            else
            	tempColor.setAlpha(255);
            multiplier = ColorUtils.RGBAToInt(tempColor);
        }else{
        	if(layer != BlockRenderLayer.CUTOUT_MIPPED || state.getBlock().canRenderInLayer(state, BlockRenderLayer.CUTOUT))
        		tint = 0;
        	multiplier = cube.color;
        }
        
        //multiplier = ColorUtils.WHITE;
        int count = format.getElementCount();

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
            
            if(OptifineHelper.isEmissive(quad.getSprite()))
            {
            	lightmap[v][0] = 1;
            	lightmap[v][1] = 1;
            }
            else
            	updateLightmap(normal[v], lightmap[v], x, y, z);
            if(dataLength[lightmapIndex] > 1)
            {
                if(blockLight > lightmap[v][0]) lightmap[v][0] = blockLight;
                if(skyLight > lightmap[v][1]) lightmap[v][1] = skyLight;
            }
            updateColor(normal[v], color[v], x, y, z, tint, multiplier);
            if(diffuse)
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
                        parent.put(e, position[v]);
                        break;
                    case NORMAL: if(normalIndex != -1)
                    {
                        parent.put(e, normal[v]);
                        break;
                    }
                    case COLOR:
                        parent.put(e, color[v]);
                        break;
                    case UV: if(element.getIndex() == 1)
                    {
                        parent.put(e, lightmap[v]);
                        break;
                    }
                    default:
                        parent.put(e, quadData[e][v]);
                }
            }
        }
        tint = -1;
    }

    protected void applyAnaglyph(float[] color)
    {
        float r = color[0];
        color[0] = (r * 30 + color[1] * 59 + color[2] * 11) / 100;
        color[1] = (r * 3 + color[1] * 7) / 10;
        color[2] = (r * 3 + color[2] * 7) / 10;
    }

    public void setQuadTint(int tint)
    {
        this.tint = tint;
    }
    public void setQuadOrientation(EnumFacing orientation) {}
    public void setQuadCulled() {}
    public void setTexture( TextureAtlasSprite texture ) {}
    public void setApplyDiffuseLighting(boolean diffuse)
    {
        this.diffuse = diffuse;
    }

    public void setWorld(IBlockAccess world)
    {
        blockInfo.setWorld(world);
    }

    public void setState(IBlockState state)
    {
    	this.state = state;
        blockInfo.setState(state);
    }

    public void setBlockPos(BlockPos blockPos)
    {
        blockInfo.setBlockPos(blockPos);
    }

    protected void updateLightmap(float[] normal, float[] lightmap, float x, float y, float z)
    {
        lightmap[0] = calcLightmap(blockInfo.getBlockLight(), x, y, z);
        lightmap[1] = calcLightmap(blockInfo.getSkyLight(), x, y, z);
    }
    
    protected void updateColor(float[] normal, float[] color, float x, float y, float z, float tint, int multiplier)
    {
        if(tint != -1)
        {
        	color[3] *= (float)(multiplier >> 0x18 & 0xFF) / 0xFF; // Alpha
            color[0] *= (float)(multiplier >> 0x10 & 0xFF) / 0xFF;
            color[1] *= (float)(multiplier >> 0x8 & 0xFF) / 0xFF;
            color[2] *= (float)(multiplier & 0xFF) / 0xFF;
        }
        float a = getAo(x, y, z);
        color[0] *= a;
        color[1] *= a;
        color[2] *= a;
    }

    protected float calcLightmap(float[][][][] light, float x, float y, float z)
    {
        x *= 2;
        y *= 2;
        z *= 2;
        float l2 = x * x + y * y + z * z;
        if(l2 > 6 - 2e-2f)
        {
            float s = (float)Math.sqrt((6 - 2e-2f) / l2);
            x *= s;
            y *= s;
            z *= s;
        }
        float ax = x > 0 ? x : -x;
        float ay = y > 0 ? y : -y;
        float az = z > 0 ? z : -z;
        float e1 = 1 + 1e-4f;
        if(ax > 2 - 1e-4f && ay <= e1 && az <= e1)
        {
            if(x > -2 + 1e-4f) x = -2 + 1e-4f;
            if(x <  2 - 1e-4f) x =  2 - 1e-4f;
        }
        else if(ay > 2 - 1e-4f && az <= e1 && ax <= e1)
        {
            if(y > -2 + 1e-4f) y = -2 + 1e-4f;
            if(y <  2 - 1e-4f) y =  2 - 1e-4f;
        }
        else if(az > 2 - 1e-4f && ax <= e1 && ay <= e1)
        {
            if(z > -2 + 1e-4f) z = -2 + 1e-4f;
            if(z <  2 - 1e-4f) z =  2 - 1e-4f;
        }
        ax = x > 0 ? x : -x;
        ay = y > 0 ? y : -y;
        az = z > 0 ? z : -z;
        if(ax <= e1 && ay + az > 3f - 1e-4f)
        {
            float s = (3f - 1e-4f) / (ay + az);
            y *= s;
            z *= s;
        }
        else if(ay <= e1 && az + ax > 3f - 1e-4f)
        {
            float s = (3f - 1e-4f) / (az + ax);
            z *= s;
            x *= s;
        }
        else if(az <= e1 && ax + ay > 3f - 1e-4f)
        {
            float s = (3f - 1e-4f) / (ax + ay);
            x *= s;
            y *= s;
        }
        else if(ax + ay + az > 4 - 1e-4f)
        {
            float s = (4 - 1e-4f) / (ax + ay + az);
            x *= s;
            y *= s;
            z *= s;
        }

        float l = 0;
        float s = 0;

        for(int ix = 0; ix <= 1; ix++)
        {
            for(int iy = 0; iy <= 1; iy++)
            {
                for(int iz = 0; iz <= 1; iz++)
                {
                    float vx = x * (1 - ix * 2);
                    float vy = y * (1 - iy * 2);
                    float vz = z * (1 - iz * 2);

                    float s3 = vx + vy + vz + 4;
                    float sx = vy + vz + 3;
                    float sy = vz + vx + 3;
                    float sz = vx + vy + 3;

                    float bx = (2 * vx + vy + vz + 6) / (s3 * sy * sz * (vx + 2));
                    s += bx;
                    l += bx * light[0][ix][iy][iz];

                    float by = (2 * vy + vz + vx + 6) / (s3 * sz * sx * (vy + 2));
                    s += by;
                    l += by * light[1][ix][iy][iz];

                    float bz = (2 * vz + vx + vy + 6) / (s3 * sx * sy * (vz + 2));
                    s += bz;
                    l += bz * light[2][ix][iy][iz];
                }
            }
        }

        l /= s;

        if(l > 15f * 0x20 / 0xFFFF) l = 15f * 0x20 / 0xFFFF;
        if(l < 0) l = 0;

        return l;
    }

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
    
    public void updateBlockInfo()
    {
        blockInfo.updateShift();
        blockInfo.updateLightMatrix();
    }
	
}
