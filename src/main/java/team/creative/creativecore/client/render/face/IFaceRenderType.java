package team.creative.creativecore.client.render.face;

import java.util.List;

import team.creative.creativecore.common.util.math.geo.VectorFan;

public interface IFaceRenderType {
    
    public boolean shouldRender();
    
    public boolean isOutside();
    
    public boolean hasCachedFans();
    
    public List<VectorFan> getCachedFans();
    
    public float getScale();
}
