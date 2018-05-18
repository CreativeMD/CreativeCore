package com.creativemd.creativecore.client.rendering.model;

import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraft.util.EnumFacing;

/**
 * Assumes VertexFormatElement is present in the VertexBuffer's vertex format.
 */
public class CreativeVertexBufferConsumer
{
    private static final float[] dummyColor = new float[]{ 1, 1, 1, 1 };
    private final CreativeCubeConsumer consumer;
    private final int[] quadData;
    private int v = 0;
    private BlockPos offset = BlockPos.ORIGIN;

    public CreativeVertexBufferConsumer(CreativeCubeConsumer consumer)
    {
        super();
        this.consumer = consumer;
        quadData = new int[consumer.format.getNextOffset()/* / 4 * 4 */];
    }

    public void put(int e, float... data)
    {
        VertexFormat format = consumer.format;
        VertexBuffer renderer = consumer.buffer;
        if(renderer.isColorDisabled() && format.getElement(e).getUsage() == EnumUsage.COLOR)
        {
            data = dummyColor;
        }
        LightUtil.pack(data, quadData, format, v, e);
        if(e == format.getElementCount() - 1)
        {
            v++;
            if(v == 4)
            {
            	BufferBuilderUtils.addVertexDataSmall(renderer, quadData);
                //renderer.addVertexData(quadData);
                renderer.putPosition(offset.getX(), offset.getY(), offset.getZ());
                //Arrays.fill(quadData, 0);
                v = 0;
            }
        }
    }

    public void setOffset(BlockPos offset)
    {
        this.offset = new BlockPos(offset);
    }
}