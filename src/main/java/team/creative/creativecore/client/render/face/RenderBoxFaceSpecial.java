package team.creative.creativecore.client.render.face;

import java.util.List;

import team.creative.creativecore.common.util.math.geo.VectorFan;

public class RenderBoxFaceSpecial extends RenderBoxFace {
    
    private final List<VectorFan> fans;
    private final float scale;
    
    public RenderBoxFaceSpecial(List<VectorFan> fans, float scale) {
        this.fans = fans;
        this.scale = scale;
    }
    
    @Override
    public boolean shouldRender() {
        return !fans.isEmpty();
    }
    
    @Override
    public boolean hasCachedFans() {
        return true;
    }
    
    @Override
    public List<VectorFan> getCachedFans() {
        return fans;
    }
    
    @Override
    public float getScale() {
        return scale;
    }
}
