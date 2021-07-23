package team.creative.creativecore.client.render.model;

import java.util.ArrayList;

import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.pipeline.LightUtil;

@OnlyIn(value = Dist.CLIENT)
public class QuadCache {
    
    private int v = 0;
    public VertexFormat format;
    public final ArrayList<int[]> quadDatas = new ArrayList<>();
    private int[] quadData;
    
    public QuadCache(VertexFormat format) {
        this.format = format;
    }
    
    public void addCache(int index, float... cache) {
        if (quadData == null)
            quadData = new int[format.getVertexSize()];
        LightUtil.pack(cache, quadData, format, v, index);
        if (index == format.getElements().size() - 1) {
            v++;
            if (v == 4) {
                quadDatas.add(quadData);
                quadData = null;
                v = 0;
            }
        }
    }
    
}
